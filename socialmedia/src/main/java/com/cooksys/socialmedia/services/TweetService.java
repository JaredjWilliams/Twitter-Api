package com.cooksys.socialmedia.services;

import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import java.util.List;

public interface TweetService {

    List<TweetResponseDto> getTweetsFromUserAndFollowers(String username);

    List<TweetResponseDto> getTweetResposts(Long id);
}
