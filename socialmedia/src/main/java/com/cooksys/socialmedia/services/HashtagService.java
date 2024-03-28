package com.cooksys.socialmedia.services;

import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;

import java.util.List;

public interface HashtagService {
    List<TweetResponseDto> getHashtagsByLabel(String label);
}
