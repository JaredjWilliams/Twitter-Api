package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.tweet.TweetRequestDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;
import com.cooksys.socialmedia.entities.Hashtag;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.exceptions.NotAuthorizedException;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.HashtagRepository;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.TweetService;
import com.cooksys.socialmedia.utils.Filter;
import com.cooksys.socialmedia.utils.Process;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

    private final HashtagRepository hashtagRepository;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final TweetMapper tweetMapper;
    private final UserMapper userMapper;


    @Override
    public List<TweetResponseDto> getTweetsFromUserAndFollowers(String username) {
        User user = getUser(username);
        List<User> followers = user.getFollowers();

        List<TweetResponseDto> tweets = new ArrayList<>();
        tweets.addAll(tweetMapper.entitiesToResponseDtos(user.getTweets()));

        for (User follower : followers) {
            tweets.addAll(tweetMapper.entitiesToResponseDtos(follower.getTweets()));
        }

        // Sorting tweets by newest to oldest posted timestamps
        tweets.sort(new Comparator<TweetResponseDto>() {
            public int compare(TweetResponseDto t1, TweetResponseDto t2) {
                return t2.getPosted().compareTo(t1.getPosted());
            }
        });

        return tweets;
    }

    @Override
    public TweetResponseDto replyToTweet(Long id, TweetRequestDto tweetRequestDto) {
        Tweet tweet = getTweet(id);

        validateTweet(tweet);
        validateUsername(tweetRequestDto.getCredentialsDto().getUsername());
        validateTweet(tweetRequestDto);

        Tweet replyTweet = createReplyTweet(tweetRequestDto, tweet);

        tweet.getReplies().add(replyTweet);
        tweetRepository.saveAndFlush(tweet);

        saveHashtags(replyTweet);

        return tweetMapper.entityToResponseDto(tweetRepository.save(replyTweet));
    }

    public List<UserResponseDto> getUsersFromTweetLikes(Long id) {
        Tweet tweet = getTweet(id);
        validateTweet(tweet);
        return userMapper.entitiesToResponseDtos(Filter.byNotDeleted(tweet.getUserLikes()));
    }

    @Override
    public List<TweetResponseDto> getTweetResposts(Long id) {
        Tweet tweet = getTweet(id);
        validateTweet(tweet);
        return tweetMapper.entitiesToResponseDtos(Filter.byNotDeleted(tweet.getReposts()));
    }

    /*@Override
    public ContextDto getContext(TweetRequestDto tweetDto) {
        Tweet tweet = tweetMapper.requestDtoToEntity(tweetDto);
        if (!isTweetDeleted(tweet)){
            ContextDto contextDto 
        }
        throw new NotFoundException("This tweet has been deleted");
    }
    public boolean isTweetDeleted(tweet){
        return tweet != null && tweet.getDeleted();
    }*/

    @Override
    public List<UserResponseDto> getMentions(Long id) {
        Tweet tweet = getTweet(id);
        validateTweet(tweet);
        return userMapper.entitiesToResponseDtos(tweet.getUserMentions());
    }

    @Override
    public List<TweetResponseDto> getTweets() {
        return tweetMapper.entitiesToResponseDtos(tweetRepository.findByDeletedFalseOrderByPostedDesc());
    }

    @Override
    public List<TweetResponseDto> getTweetReplies(Long id) {
        Tweet tweet = getTweet(id);
        validateTweet(tweet);
        return tweetMapper.entitiesToResponseDtos(tweet.getReplies());
    }

    @Override
    public TweetResponseDto postRepostOfTweet(Long id, CredentialsDto credentialsDto) {
        Tweet tweet = new Tweet();
        tweet.setAuthor(userRepository.findByCredentialsUsername(credentialsDto.getUsername()));
        tweet.setRepostOf(getTweet(id));
        tweetRepository.saveAndFlush(tweet);
        return tweetMapper.entityToResponseDto(tweet);
    }

    @Override
    public TweetResponseDto getTweetById(Long id) {
        Tweet tweet = getTweet(id);
        validateTweet(tweet);
        return tweetMapper.entityToResponseDto(tweet);
    }

    private Tweet createReplyTweet(TweetRequestDto tweetRequestDto, Tweet tweet) {
        Tweet replyTweet = tweetMapper.requestDtoToEntity(tweetRequestDto);
        replyTweet.setAuthor(getUser(tweetRequestDto.getCredentialsDto().getUsername()));
        replyTweet.setInReplyTo(tweet);
        replyTweet.setUserMentions(createUserMentions(replyTweet));
        return tweetRepository.saveAndFlush(replyTweet);
    }

    public User getUser(String username) {
        User user = userRepository.findByCredentialsUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found with username: " + username);
        }
        return user;
    }


    private Tweet getTweet(Long id) {
        Optional<Tweet> tweet = tweetRepository.findById(id);

        if (tweet.isEmpty()) {
            throw new BadRequestException("Tweet is not found");
        }

        return tweet.get();
    }

    private List<User> createUserMentions(Tweet tweet) {
        List<User> users = new ArrayList<>();

        Process.forUsers(tweet).forEach(mention -> {
            if (userRepository.existsByCredentialsUsername(mention)) {
                User user = userRepository.findByCredentialsUsername(mention);
                users.add(user);
            }});

        return users;
    }

    private void saveHashtags(Tweet tweet) {
        Process.forHashtags(tweet).forEach(hashtag -> {
            Hashtag tag = hashtagRepository.findByLabel(hashtag);
            if (tag != null) {
                tag.getTweets().add(tweet);
            } else {
                tag = new Hashtag();
                tag.setLabel(hashtag);
                tag.setTweets(List.of(tweet));
            }

            hashtagRepository.save(tag);
        });
    }

    private void validateTweet(Tweet tweet) {
        if (tweet.getDeleted()) {
            throw new NotFoundException("Tweet has been deleted");
        }
    }

    private void validateCredentials(User user, CredentialsDto credentialsDto) {
        if (!user.getCredentials().getPassword().equals(credentialsDto.getPassword())){
            throw new NotAuthorizedException("Incorrect password for user: " + credentialsDto.getUsername());
        }
    }

    @Override
    public void createLike(Long id, CredentialsDto credentialsDto) {
        Tweet tweet = getTweet(id);
        validateTweet(tweet);

        User user = getUser(credentialsDto.getUsername());
        validateCredentials(user, credentialsDto);

        List<User> userLikes = tweet.getUserLikes();
        userLikes.add(user);
        tweet.setUserLikes(userLikes);
        tweetRepository.saveAndFlush(tweet);

        List<Tweet> tweetLikes = user.getTweetLikes();
        tweetLikes.add(tweet);
        user.setTweetLikes(tweetLikes);
        userRepository.saveAndFlush(user);
    }

    @Override
    public TweetResponseDto deleteTweet(Long id, CredentialsDto credentialsDto) {
        Tweet tweetToBeDeleted = getTweet(id);
        validateTweet(tweetToBeDeleted);

        if (!tweetToBeDeleted.getAuthor().equals(userRepository.findByCredentialsUsername(credentialsDto.getUsername()))){
            throw new BadRequestException("The specified user does not exist or is not the author of this tweet.");
        }
        
        tweetToBeDeleted.setDeleted(true);
        tweetRepository.saveAndFlush(tweetToBeDeleted);
        return tweetMapper.entityToResponseDto(tweetToBeDeleted);
    }

    private void validateTweet(TweetRequestDto tweetRequestDto) {
        if (tweetRequestDto.getContent().isBlank()) {
            throw new BadRequestException("Tweet content cannot be blank");
        }
    }

    private void validateUsername(String username) {
        if (!userRepository.existsByCredentialsUsername(username)) {
            throw new NotFoundException("Username not found");
        }
    }

}
