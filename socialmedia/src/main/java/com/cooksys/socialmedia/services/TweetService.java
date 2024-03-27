package com.cooksys.socialmedia.services;

import java.util.List;

import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;

public interface TweetService {

    List<TweetResponseDto> getTweetsFromUserAndFollowers(String username);
}
