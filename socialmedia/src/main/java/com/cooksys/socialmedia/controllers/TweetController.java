package com.cooksys.socialmedia.controllers;


import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
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

    @GetMapping("/{id}/reposts")
    @ResponseStatus(HttpStatus.OK)
    public List<TweetResponseDto> getTweetReposts(@PathVariable Long id) {
        return tweetService.getTweetResposts(id);
    }
}
