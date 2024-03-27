package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.dtos.user.UserRequestDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.TweetRepository;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    private final TweetMapper tweetMapper;
    private final UserMapper userMapper;

    private User getUserByCredentials(CredentialsDto credentialsDto) {
        return userRepository.findByCredentialsUsername(credentialsDto.getUsername());
    }

    @Override
    public List<UserResponseDto> getUsers() {
        return userMapper.entitiesToResponseDtos(userRepository.findByDeletedFalse());
    }

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {

        User user = getUserByCredentials(userRequestDto.getCredentials());

        if (isUserCreatedAndNotDeleted(user)) {
            throw new BadRequestException("User can't be created because the user already exists");
        }

        if (isUserDeleted(user)) {
            user.setDeleted(false);
            return userMapper.entityToResponseDto(userRepository.save(user));
        }

        return userMapper.entityToResponseDto(userRepository.saveAndFlush(userMapper.requestDtoToEntity(userRequestDto)));
    }


    @Override
    public List<TweetResponseDto> getTweetsFromUser(String username) {
        User user = userRepository.findByCredentialsUsername(username);

        if (user == null) {
            throw new BadRequestException("User not found with username: " + username);
        }

        if (user.getDeleted()) {
            throw new BadRequestException("User has been deleted");
        }

        return tweetMapper.entitiesToResponseDtos(
                tweetRepository.findByAuthorOrderByPostedDesc(user));
    }

    private boolean isUserCreatedAndNotDeleted(User user) {
        return user != null && !user.getDeleted();
    }

    private boolean isUserDeleted(User user) {
        return user != null && user.getDeleted();
    }


}
