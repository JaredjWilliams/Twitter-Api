package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {

    private final UserRepository userRepository;


    @Override
    public boolean usernameIsAvailable(String username) {
        return userRepository.findByCredentialsUsername(username) == null;
    }

    @Override
    public boolean doesUsernameExist(String username) {
        return userRepository.findByCredentialsUsername(username) != null;

    }
}
