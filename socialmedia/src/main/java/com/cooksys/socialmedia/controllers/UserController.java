package com.cooksys.socialmedia.controllers;


import com.cooksys.socialmedia.dtos.CredentialsDto;
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

    @GetMapping("/@{username}")
    public UserResponseDto getUser(@PathVariable("username") String username) {
        return userService.getUser(username);
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

    @GetMapping("/@{username}/mentions")
    @ResponseStatus(HttpStatus.OK)
    public List<TweetResponseDto> getMentions(@PathVariable("username") String username) {
        return userService.getUserMentions(username);
    }

    @GetMapping("/@{username}/followers")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getFollowers(@PathVariable("username") String username){
        return userService.getFollowers(username);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@RequestBody UserRequestDto userRequestDto) {
        return userService.createUser(userRequestDto);
    }


    @GetMapping("/@{username}/following")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getFollowing(@PathVariable("username") String username){
        return userService.getFollowing(username);
    }

    @DeleteMapping("/@{username}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto deleteUser(@PathVariable("username") String username) {
        return userService.deleteUser(username);
    }

    @PostMapping("/@{username}/follow")
    @ResponseStatus(HttpStatus.OK)
    public void followUser(@PathVariable("username") String username, @RequestBody CredentialsDto credentialsDto) {
        userService.followUser(username, credentialsDto);
    }
    
    @PatchMapping("/@{username}")
    public UserResponseDto updateUser(@PathVariable("username") String username, @RequestBody UserRequestDto userRequestDto) {
        return userService.updateUser(username, userRequestDto);
    }

    @PostMapping("/@{username}/unfollow")
    @ResponseStatus(HttpStatus.OK)
    public void unfollowUser(@PathVariable("username") String username, @RequestBody CredentialsDto credentialsDto) {
        userService.unfollowUser(username, credentialsDto);
    }

}
