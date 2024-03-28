package com.cooksys.socialmedia.controllers;


import com.cooksys.socialmedia.dtos.tweet.TweetRequestDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;
import com.cooksys.socialmedia.services.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tweets")
public class TweetController {

    private final TweetService tweetService;

    @PostMapping("/{id}/reply")
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponseDto replyToTweet(@PathVariable Long id, @RequestBody TweetRequestDto tweetRequestDto) {
        return tweetService.replyToTweet(id, tweetRequestDto);
    }

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

}
