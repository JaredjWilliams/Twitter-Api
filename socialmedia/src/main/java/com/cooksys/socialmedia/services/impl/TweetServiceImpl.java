package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.tweet.TweetRequestDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.entities.Hashtag;
import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.repositories.HashtagRepository;

import com.cooksys.socialmedia.mappers.UserMapper;

import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.TweetService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final HashtagRepository hashtagRepository;
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

    public List<UserResponseDto> getUsersFromTweetLikes(Long id) {
        Tweet tweet = getTweet(id);
        validateTweet(tweet);
        return userMapper.entitiesToResponseDtos(tweet.getUserLikes().stream()
                .filter(user -> !user.getDeleted())
                .toList());
    }

    @Override
    public TweetResponseDto createTweet(TweetRequestDto tweetRequestDto) {

        String username = tweetRequestDto.getCredentialsDto().getUsername();
        String password = tweetRequestDto.getCredentialsDto().getPassword();

        if (tweetRequestDto.getContent() == null || tweetRequestDto.getContent().isEmpty()) {
            throw new BadRequestException("Tweet must have content in request body to be created.");
        }

        if (tweetRequestDto.getCredentialsDto() == null) {
            throw new BadRequestException("Tweet must have credentials in request body to be created.");
        }

        if (userRepository.findByCredentialsUsername(tweetRequestDto.getCredentialsDto().getUsername()) == null) {
            throw new NotFoundException("User not found with given username: " + username); 
        }
        else if(!userRepository.findByCredentialsUsername(username).getCredentials().getPassword().equals(password)) {
            throw new BadRequestException("Incorrect password for user: " + username);
        }
        

        Tweet tweet =  tweetMapper.requestDtoToEntity(tweetRequestDto);
        tweet.setAuthor(userRepository.findByCredentialsUsername(username));

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
        return tweetMapper.entitiesToResponseDtos(tweet.getReposts().stream()
                .filter(t -> !t.getDeleted())
                .toList());
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

    private void validateTweet(Tweet tweet) {
        if (tweet.getDeleted()) {
            throw new NotFoundException("Tweet has been deleted");
        }
    }

}
