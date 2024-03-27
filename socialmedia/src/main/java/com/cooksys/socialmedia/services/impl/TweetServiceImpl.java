package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.ContextDto;
import com.cooksys.socialmedia.dtos.tweet.TweetRequestDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;
import com.cooksys.socialmedia.entities.Tweet;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.services.TweetService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final TweetMapper tweetMapper;
    private final UserMapper userMapper;

    /*@Override
    public ContextDto getContext(TweetRequestDto tweetDto) {
        Tweet tweet = tweetMapper.requestDtoToEntity(tweetDto);
        if (!isTweetDeleted(tweet)){
            ContextDto contextDto 
        }
        throw new NotFoundException("This tweet has been deleted");
    }*/

    public boolean isTweetDeleted(Tweet tweet){
        return tweet != null && tweet.getDeleted();
    }

    private boolean isTweetCreatedAndNotDeleted(Tweet tweet) {
        return tweet != null && !tweet.getDeleted();
    }

    @Override
    public List<UserResponseDto> getMentions(Long id) {
        Tweet tweet = tweetRepository.getReferenceById(id);
        List<User> mentionedUsers = tweet.getUserMentions();
        List<UserResponseDto> mentionedUserResponseDtos = new ArrayList<>();
        for (User u : mentionedUsers){
            mentionedUserResponseDtos.add(userMapper.entityToResponseDto(u));
        }
        return mentionedUserResponseDtos;
    }

    @Override
    public List<TweetResponseDto> getTweets() {
        List<Tweet> tweList = tweetRepository.findAll();
        List<TweetResponseDto> tweets = new ArrayList<>();
        for (Tweet tweet : tweList){
            if (tweet.getDeleted() != true)
                tweets.add(tweetMapper.entityToResponseDto(tweet));
        }
        Collections.sort(tweets, new Comparator<TweetResponseDto>() {
            public int compare(TweetResponseDto t1, TweetResponseDto t2) {
                if (t1.getPosted() == null || t2.getPosted() == null)
                  return 0;
                return t2.getPosted().compareTo(t1.getPosted());
            }
          });
        return tweets;
    }
}
