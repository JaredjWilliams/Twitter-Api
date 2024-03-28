package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.ValidateService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {

    private final UserRepository userRepository;

    @Override
    public boolean usernameIsAvailable(String username) {
        if (userRepository.findByCredentialsUsername(username) == null)
            return true;
        else 
        return userRepository.findByCredentialsUsername(username).getDeleted();
    }
}
