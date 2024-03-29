package com.cooksys.socialmedia.controllers;

import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.services.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class HashtagController {

    private final HashtagService hashtagService;

    @GetMapping("/{label}")
    @ResponseStatus(HttpStatus.OK)
    public List<TweetResponseDto> getHashtagsByLabel(@PathVariable("label") String label) {
        return hashtagService.getHashtagsByLabel(label);
    }

    @GetMapping
    public List<HashtagDto> getHashtags() {
        return hashtagService.getHashtags();
    }

}
