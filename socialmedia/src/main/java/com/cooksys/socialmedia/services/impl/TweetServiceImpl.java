package com.cooksys.socialmedia.services.impl;


import com.cooksys.socialmedia.dtos.ContextDto;

import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.dtos.tweet.TweetRequestDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;
import com.cooksys.socialmedia.entities.Hashtag;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.exceptions.NotAuthorizedException;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.HashtagMapper;
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

import java.util.*;


@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

    private final HashtagRepository hashtagRepository;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final TweetMapper tweetMapper;
    private final UserMapper userMapper;
    private final HashtagMapper hashtagMapper;

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
        validateUsername(tweetRequestDto.getCredentials().getUsername());
        validateTweet(tweetRequestDto);

        Tweet replyTweet = createReplyTweet(tweetRequestDto, tweet);

        replyTweet.setAuthor(getUser(tweetRequestDto.getCredentials().getUsername()));
        replyTweet.setInReplyTo(tweet);
        replyTweet.setUserMentions(createUserMentions(replyTweet));

        tweetRepository.saveAndFlush(replyTweet);

        tweet.getReplies().add(replyTweet);
        tweet.getReplies().add(replyTweet);
        tweetRepository.saveAndFlush(tweet);

        saveHashtags(replyTweet);

        return tweetMapper.entityToResponseDto(tweetRepository.save(replyTweet));
    }

    @Override
    public void createLike(Long id, CredentialsDto credentialsDto) {
        Tweet tweet = getTweet(id);
        validateTweet(tweet);

        User user = getUser(credentialsDto.getUsername());
        validateCredentials(user, credentialsDto);

        List<Tweet> tweetLikes = user.getTweetLikes();
        List<User> userLikes = tweet.getUserLikes();

        if (!user.getTweetLikes().contains(tweet)) {
            userLikes.add(user);
            tweetLikes.add(tweet);
        }

        tweet.setUserLikes(userLikes);
        user.setTweetLikes(tweetLikes);

        tweetRepository.saveAndFlush(tweet);
        userRepository.saveAndFlush(user);
    }

    public List<UserResponseDto> getUsersFromTweetLikes(Long id) {
        Tweet tweet = getTweet(id);
        validateTweet(tweet);
        return userMapper.entitiesToResponseDtos(Filter.byNotDeleted(tweet.getUserLikes()));
    }

    @Override
    public TweetResponseDto createTweet(TweetRequestDto tweetRequestDto) {

        

        if (tweetRequestDto.getCredentials() == null) {
            throw new BadRequestException("Tweet must have credentials in request body to be created.");
        } 

        String username = tweetRequestDto.getCredentials().getUsername();
        String password = tweetRequestDto.getCredentials().getPassword();
        
        if (tweetRequestDto.getContent() == null || tweetRequestDto.getContent().isEmpty()) {
            throw new BadRequestException("Tweet must have content in request body to be created.");
        }

        if (userRepository.findByCredentialsUsername(tweetRequestDto.getCredentials().getUsername()) == null) {
            throw new NotFoundException("User not found with given username: " + username); 
        }
        else if(!userRepository.findByCredentialsUsername(username).getCredentials().getPassword().equals(password)) {
            throw new BadRequestException("Incorrect password for user: " + username);
        }
        

        Tweet tweet =  tweetMapper.requestDtoToEntity(tweetRequestDto);
        User user = userRepository.findByCredentialsUsername(username);
        validateCredentials(user, tweetRequestDto.getCredentials());
        tweet.setAuthor(user);

        String[] content = tweet.getContent().split("\\s+");
        List<User> usersMentioned = new ArrayList<>();

        // Map user mentions
        for (String subString : content) {
            if (subString.charAt(0) == '@') {
                User userMentioned = userRepository.findByCredentialsUsername(subString.substring(1));

                if (userMentioned != null) {
                    List<Tweet> tweets = userMentioned.getTweetMentions();
                    tweets.add(tweet);
                    userMentioned.setTweetMentions(tweets);
                    usersMentioned.add(userMentioned);
                    userRepository.saveAndFlush(userMentioned);
                }
            }
        }

        tweet.setUserMentions(usersMentioned);
        tweetRepository.saveAndFlush(tweet);


        // Map user hashtags
        for (String subString : content) {
            // Add hashtag to tweet 
            if (subString.charAt(0) == '#') {

                Hashtag hashtag = hashtagRepository.findByLabel(subString); 

                // Check if hashtag already exists in hashtagRepository
                if (hashtag == null) {
                    // Create new hashtag in repository 
                    Hashtag newHashtag = new Hashtag();
                    newHashtag.setLabel(subString);
                    newHashtag.setFirstUsed(tweet.getPosted());
                    newHashtag.setLastUsed(tweet.getPosted());
                    newHashtag.setTweets(Arrays.asList(tweet));
                    hashtagRepository.saveAndFlush(newHashtag);
                }

                // Update timestamp if hashtag is already saved
                else {
                    hashtag.setLastUsed(tweet.getPosted());
                }

            }
        }

        return tweetMapper.entityToResponseDto(tweet);
    }
    public List<TweetResponseDto> getTweetResposts(Long id) {
        Tweet tweet = getTweet(id);
        validateTweet(tweet);
        return tweetMapper.entitiesToResponseDtos(Filter.byNotDeleted(tweet.getReposts()));
    }

    
    public boolean isTweetDeleted(Tweet tweet){
        return tweet != null && tweet.getDeleted();
    }

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
        replyTweet.setAuthor(getUser(tweetRequestDto.getCredentials().getUsername()));
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

    private void validateTweet(TweetRequestDto tweetRequestDto) {
        if (tweetRequestDto.getContent().isBlank()) {
            throw new BadRequestException("Tweet content cannot be blank");
        }
    }


    private void validateCredentials(User user, CredentialsDto credentialsDto) {
        if (!user.getCredentials().getPassword().equals(credentialsDto.getPassword())){
            throw new NotAuthorizedException("Incorrect password for user: " + credentialsDto.getUsername());
        }
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

    private void validateUsername(String username) {
        if (!userRepository.existsByCredentialsUsername(username)) {
            throw new NotFoundException("Username not found");
        }
    }

    @Override
    public ContextDto getTweetContext(Long id) {
        Tweet tweet = getTweet(id);
        validateTweet(tweet);
        List<TweetResponseDto> after = new ArrayList<>();
        for (Tweet t : tweet.getReplies()){
            if ((t != null || !isTweetDeleted(t))){
                after.add(tweetMapper.entityToResponseDto(t));
            }
        }

        List<TweetResponseDto> before = new ArrayList<>();

        if (!isTweetDeleted(tweet.getInReplyTo()) && tweet.getInReplyTo() != null)
            before.add(tweetMapper.entityToResponseDto(tweet.getInReplyTo()));

        if (!isTweetDeleted(tweet.getRepostOf()) && tweet.getRepostOf() != null)
            before.add(tweetMapper.entityToResponseDto(tweet.getRepostOf()));

        ContextDto contextDto = new ContextDto();
        contextDto.setAfter(after);
        contextDto.setBefore(before);
        contextDto.setTarget(tweetMapper.entityToResponseDto(tweet));
        return contextDto;
    }

    @Override
    public List<HashtagDto> getTweetHashtags(Long id) {
        Tweet tweet = getTweet(id);
        validateTweet(tweet);

        List<Hashtag> hashtags = tweet.getHashtags(); 
        for (Hashtag hashtag : hashtags) {
            hashtag.setLabel(hashtag.getLabel().substring(1));
        }

        return hashtagMapper.entitiesToDtos(hashtags);
    }
}
