package com.cooksys.socialmedia.controllers;

import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;
import com.cooksys.socialmedia.services.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tweets")
public class TweetController {

    private final TweetService tweetService;

    @GetMapping("{id}/likes")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getUsersFromTweetLikes(@PathVariable Long id) {
        return tweetService.getUsersFromTweetLikes(id);
    }

    @GetMapping("/{id}/reposts")
    @ResponseStatus(HttpStatus.OK)
    public List<TweetResponseDto> getTweetReposts(@PathVariable Long id) {
        return tweetService.getTweetResposts(id);
    }

    @GetMapping
    public List<TweetResponseDto> getTweets(){
        return tweetService.getTweets();
    }

    @GetMapping("/{id}/mentions")
    public List<UserResponseDto> getMentions(@PathVariable("id") Long id){
        return tweetService.getMentions(id);
    }

    @GetMapping("/{id}/replies")
    public List<TweetResponseDto> getTweetReplies(@PathVariable("id") Long id) {
        return tweetService.getTweetReplies(id);
    } 

    @PostMapping("/{id}/repost")
    public TweetResponseDto postRepostOfTweet(@PathVariable("id") Long id, @RequestBody CredentialsDto credentialsDto){
        return tweetService.postRepostOfTweet(id, credentialsDto);
    }

    // @PostMapping("/{id}/likes")
    // public Boolean createLike(@PathVariable("id") Long id, @RequestBody CredentialsDto credentialsDto){
    //     return tweetService.createLike(id, credentialsDto);
    // }

    @PostMapping("/{id}/likes")
    public void createLike(@PathVariable("id") Long id, @RequestBody CredentialsDto credentialsDto){
        tweetService.createLike(id, credentialsDto);
    }   
}
