package com.cooksys.socialmedia.controllers;


import com.cooksys.socialmedia.services.ValidateService;
import lombok.RequiredArgsConstructor;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/validate")
public class ValidateController {

    private final ValidateService validateService;


    @GetMapping("/username/available/@{username}")
    public boolean usernameIsAvailable(@PathVariable("username") String username) {
        return validateService.usernameIsAvailable(username);
    }
    @GetMapping("/username/exists/@{username}")
    @ResponseStatus(HttpStatus.OK)
    public boolean doesUsernameExist(@PathVariable("username") String username) {
        return validateService.doesUsernameExist(username);

    }
    @GetMapping("/tag/exists/{label}")
    public boolean doesHashtagExist(@PathVariable("label") String label) {
        return validateService.doesHashtagExist("#" + label);
    }
}

