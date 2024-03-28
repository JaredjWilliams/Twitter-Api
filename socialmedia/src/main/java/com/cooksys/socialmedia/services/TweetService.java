package com.cooksys.socialmedia.services;

import com.cooksys.socialmedia.dtos.tweet.TweetRequestDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;

import java.util.List;

public interface TweetService {

    List<TweetResponseDto> getTweetsFromUserAndFollowers(String username);

    TweetResponseDto replyToTweet(Long id, TweetRequestDto tweetRequestDto);
}
