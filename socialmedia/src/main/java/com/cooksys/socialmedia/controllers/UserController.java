package com.cooksys.socialmedia.controllers;


import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.dtos.user.UserRequestDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;
import com.cooksys.socialmedia.services.TweetService;
import com.cooksys.socialmedia.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final TweetService tweetService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/@{username}/feed")
    @ResponseStatus(HttpStatus.OK)
    public List<TweetResponseDto> getTweetsFromUserAndFollowers(@PathVariable("username") String username) {
       return tweetService.getTweetsFromUserAndFollowers(username);
    }

    @GetMapping("/@{username}/tweets")
    @ResponseStatus(HttpStatus.OK)
    public List<TweetResponseDto> getTweetsFromUser(@PathVariable("username") String username) {
        return userService.getTweetsFromUser(username);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@RequestBody UserRequestDto userRequestDto) {
        return userService.createUser(userRequestDto);
    }

    @DeleteMapping("/@{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto deleteUser(@PathVariable("username") String username) {
        return userService.deleteUser(username);
    }
}
