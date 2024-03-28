package com.cooksys.socialmedia.controllers;


import com.cooksys.socialmedia.services.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/validate")
public class ValidateController {

    private final ValidateService validateService;

    @GetMapping("/username/exists/@{username}")
    @ResponseStatus(HttpStatus.OK)
    public boolean doesUsernameExist(@PathVariable String username) {
        return validateService.doesUsernameExist(username);
    }
}
