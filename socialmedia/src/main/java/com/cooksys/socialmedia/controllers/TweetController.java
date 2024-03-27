package com.cooksys.socialmedia.controllers;


import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;
import com.cooksys.socialmedia.services.TweetService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tweets")
public class TweetController {

    private final TweetService tweetService;

    @GetMapping
    public List<TweetResponseDto> getTweets(){
        return tweetService.getTweets();
    }

    @GetMapping("/{id}/mentions")
    public List<UserResponseDto> getMentions(@PathVariable("id") Long id){
        return tweetService.getMentions(id);
    }
}
