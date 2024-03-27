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
        List<User> curUsers = userRepository.findAll();
        Boolean ret = true;
        for (User u : curUsers){
            if (u.getCredentials().getUsername().equals(username)){
                ret = false;
                break;
            }
        }
        return ret;
    }
}
