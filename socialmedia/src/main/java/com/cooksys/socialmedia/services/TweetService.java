package com.cooksys.socialmedia.services;

import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;

import java.util.List;

import com.cooksys.socialmedia.dtos.CredentialsDto;


public interface TweetService {

    List<TweetResponseDto> getTweetsFromUserAndFollowers(String username);

    List<UserResponseDto> getUsersFromTweetLikes(Long id);

    List<TweetResponseDto> getTweetResposts(Long id);

    //ContextDto getContext(TweetRequestDto tweetRequestDto);

    List<UserResponseDto> getMentions(Long id);

    List<TweetResponseDto> getTweets();


    List<TweetResponseDto> getTweetReplies(Long id);

    TweetResponseDto postRepostOfTweet(Long id, CredentialsDto credentialsDto);


}
