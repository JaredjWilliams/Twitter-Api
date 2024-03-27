package com.cooksys.socialmedia.services;

import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import java.util.List;

import java.util.List;

import com.cooksys.socialmedia.dtos.ContextDto;
import com.cooksys.socialmedia.dtos.tweet.TweetRequestDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;

public interface TweetService {

    List<TweetResponseDto> getTweetsFromUserAndFollowers(String username);
    //ContextDto getContext(TweetRequestDto tweetRequestDto);

    List<UserResponseDto> getMentions(Long id);

    List<TweetResponseDto> getTweets();
}
