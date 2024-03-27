package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.user.UserResponseDto;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getUsers() {
        return userMapper.entitiesToResponseDtos(userRepository.findByDeletedFalse());
    }
}
