package com.cooksys.socialmedia.services;


import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;

import java.util.List;
import java.util.List;

import com.cooksys.socialmedia.dtos.HashtagDto;

public interface HashtagService {
    List<TweetResponseDto> getHashtagsByLabel(String label);
    List<HashtagDto> getHashtags();

}
