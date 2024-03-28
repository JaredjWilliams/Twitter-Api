package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;

import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.TweetMapper;

import com.cooksys.socialmedia.mappers.UserMapper;

import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

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
    public List<TweetResponseDto> getTweetResposts(Long id) {
        Tweet tweet = getTweet(id);
        validateTweet(tweet);
        return tweetMapper.entitiesToResponseDtos(tweet.getReposts().stream().filter(t -> !t.getDeleted()).toList());
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
