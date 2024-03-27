package com.cooksys.socialmedia.services;

import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.dtos.user.UserRequestDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;
import com.cooksys.socialmedia.entities.User;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getUsers();

    UserResponseDto createUser(UserRequestDto userCreationRequestDto);

    UserResponseDto getUser(String username);
    List<TweetResponseDto> getTweetsFromUser(String username);

    List<UserResponseDto> getFollowing(String username);
}
