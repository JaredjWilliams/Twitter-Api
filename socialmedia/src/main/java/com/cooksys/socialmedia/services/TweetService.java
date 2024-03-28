package com.cooksys.socialmedia.services;

import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;

import java.util.List;

public interface TweetService {

    List<TweetResponseDto> getTweetsFromUserAndFollowers(String username);

    List<UserResponseDto> getUsersFromTweetLikes(Long id);
}
