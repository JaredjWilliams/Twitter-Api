package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.dtos.user.UserRequestDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.exceptions.NotAuthorizedException;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.UserService;
import com.cooksys.socialmedia.utils.Sort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
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
            throw new NotFoundException("User not found");
        }

        if (user.getDeleted()) {
            throw new BadRequestException("User is deleted");
        }
        return tweetMapper.entitiesToResponseDtos(user.getTweets());
    }



    @Override
    public UserResponseDto deleteUser(String username) {
        User user = userRepository.findByCredentialsUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        if (user.getDeleted()) {
            throw new BadRequestException("User is deleted");
        }
        user.setDeleted(true);
        return userMapper.entityToResponseDto(userRepository.save(user));
    }


    @Override
    public List<TweetResponseDto> getUserMentions(String username) {
        User user = userRepository.findByCredentialsUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        if (user.getDeleted()) {
            throw new BadRequestException("User is deleted");
        }

        return tweetMapper.entitiesToResponseDtos(Sort.filterNotDeletedAndSortDesc(user.getTweetMentions()));
    }

    @Override
    public UserResponseDto getUser(String username) {
        User user = userRepository.findByCredentialsUsername(username);

        if (user == null) {
            throw new NotFoundException("User not found with username: " + username);
        } 

        else if (isUserDeleted(user)) {
            throw new BadRequestException("User: " + username + " is deleted");
        }

        return userMapper.entityToResponseDto(user);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        if (user.getDeleted()) {
            throw new BadRequestException("User has been deleted");
        }
    }

    private void validateCredentials(User user, CredentialsDto credentialsDto) {
        if (!user.getCredentials().getPassword().equals(credentialsDto.getPassword())){
            throw new NotAuthorizedException("Incorrect password for user: " + credentialsDto.getUsername());
        }
    }

    @Override
    public List<UserResponseDto> getFollowing(String username) {
        User user = userRepository.findByCredentialsUsername(username);
        validateUser(user);
        List<User> followingUsers = user.getFollowing();
        Iterator<User> iterator = followingUsers.iterator();
        while(iterator.hasNext()){
            User u = iterator.next();
            if (u.getDeleted()) {
                iterator.remove();
            }
        }
        return userMapper.entitiesToResponseDtos(followingUsers);
    }

    private boolean isUserCreatedAndNotDeleted(User user) {
        return user != null && !user.getDeleted();
    }

    private boolean isUserDeleted(User user) {
        return user != null && user.getDeleted();
    }

    @Override
    public void unfollowUser(String username, CredentialsDto credentialsDto) {
        User userUnfollowing = userRepository.findByCredentialsUsername(username);
        User currentlyFollowedUser = userRepository.findByCredentialsUsername(credentialsDto.getUsername());
        validateUser(currentlyFollowedUser);
        
        List<User> userFollowing = userUnfollowing.getFollowing();

        if(!userFollowing.contains(currentlyFollowedUser) || !currentlyFollowedUser.getCredentials().getPassword().equals(credentialsDto.getPassword())){
            throw new BadRequestException("The given user to follow currently isn't followed by the user," + 
            " or the given credentials are invalid");
        }
        
        userFollowing.remove(currentlyFollowedUser);
        userUnfollowing.setFollowing(userFollowing);
        userRepository.saveAndFlush(userUnfollowing);
    }

    @Override
    public void followUser(String username, CredentialsDto credentialsDto) {
        User subscriber = userRepository.findByCredentialsUsername(credentialsDto.getUsername());
        User user = userRepository.findByCredentialsUsername(username);
        validateUser(subscriber);
        validateUser(user);
        validateCredentials(subscriber, credentialsDto);

        List<User> followers = user.getFollowers();
        List<User> following = subscriber.getFollowing();

        if (followers.contains(subscriber) || following.contains(user)) {
            throw new BadRequestException("User " + subscriber.getCredentials().getUsername() + " is already following " + user.getCredentials().getUsername());
        } 

        if (user.getCredentials().equals(subscriber.getCredentials())) {
            throw new BadRequestException("User cannot follow themself");
        }
        else {
            following.add(user);
            subscriber.setFollowing(following);
            userRepository.saveAndFlush(subscriber);

            followers.add(subscriber);
            user.setFollowers(followers);
            userRepository.saveAndFlush(user);
        }

    }

}
