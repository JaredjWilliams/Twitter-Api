package com.cooksys.socialmedia.services;

import com.cooksys.socialmedia.dtos.tweet.TweetRequestDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
<<<<<<< HEAD
=======
import com.cooksys.socialmedia.dtos.user.UserResponseDto;
>>>>>>> master

import java.util.List;

import java.util.List;

import com.cooksys.socialmedia.dtos.ContextDto;
import com.cooksys.socialmedia.dtos.tweet.TweetRequestDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;

public interface TweetService {

    List<TweetResponseDto> getTweetsFromUserAndFollowers(String username);

<<<<<<< HEAD
    TweetResponseDto replyToTweet(Long id, TweetRequestDto tweetRequestDto);
=======
    List<UserResponseDto> getUsersFromTweetLikes(Long id);

    List<TweetResponseDto> getTweetResposts(Long id);

    //ContextDto getContext(TweetRequestDto tweetRequestDto);

    List<UserResponseDto> getMentions(Long id);

    List<TweetResponseDto> getTweets();


    List<TweetResponseDto> getTweetReplies(Long id);



>>>>>>> master
}
