package com.cooksys.socialmedia.services;

import com.cooksys.socialmedia.dtos.tweet.TweetRequestDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import java.util.List;

public interface TweetService {

    List<TweetResponseDto> getTweetsFromUserAndFollowers(String username);

    List<TweetResponseDto> getTweetReplies(Long id);

    TweetResponseDto createTweet(TweetRequestDto tweetRequestDto);
}
