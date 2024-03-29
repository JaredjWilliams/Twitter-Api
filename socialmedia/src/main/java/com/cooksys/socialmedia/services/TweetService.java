package com.cooksys.socialmedia.services;


import com.cooksys.socialmedia.dtos.ContextDto;

import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.dtos.tweet.TweetRequestDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;

import java.util.List;


public interface TweetService {

    List<TweetResponseDto> getTweetsFromUserAndFollowers(String username);

    TweetResponseDto replyToTweet(Long id, TweetRequestDto tweetRequestDto);

    List<UserResponseDto> getUsersFromTweetLikes(Long id);

    List<TweetResponseDto> getTweetResposts(Long id);

    ContextDto getTweetContext(Long id);

    List<UserResponseDto> getMentions(Long id);

    List<TweetResponseDto> getTweets();

    List<TweetResponseDto> getTweetReplies(Long id);

    TweetResponseDto deleteTweet(Long id, CredentialsDto credentialsDto);

    TweetResponseDto postRepostOfTweet(Long id, CredentialsDto credentialsDto);

    TweetResponseDto createTweet(TweetRequestDto tweetRequestDto);

    void createLike(Long id, CredentialsDto credentialsDto);

    TweetResponseDto getTweetById(Long id);

    List<HashtagDto> getTweetHashtags(Long id);




}
