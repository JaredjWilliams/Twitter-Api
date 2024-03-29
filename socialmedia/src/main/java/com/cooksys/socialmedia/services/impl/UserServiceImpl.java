package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.dtos.user.UserRequestDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;
import com.cooksys.socialmedia.entities.Profile;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.exceptions.NotAuthorizedException;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.TweetMapper;
import com.cooksys.socialmedia.mappers.UserMapper;
import com.cooksys.socialmedia.repositories.UserRepository;
import com.cooksys.socialmedia.services.UserService;
import com.cooksys.socialmedia.utils.Sort;
import com.cooksys.socialmedia.utils.Validate;
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
        Validate.validateCreatingAUser(userRequestDto);
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
        validateUser(user);
        return tweetMapper.entitiesToResponseDtos(user.getTweets());
    }

    @Override
    public UserResponseDto deleteUser(String username) {
        User user = userRepository.findByCredentialsUsername(username);
        validateUser(user);
        user.setDeleted(true);
        return userMapper.entityToResponseDto(userRepository.save(user));
    }

    @Override
    public List<UserResponseDto> getFollowers(String username) {
        validateUser(userRepository.findByCredentialsUsername(username));
        return userMapper.entitiesToResponseDtos(userRepository.findByFollowingCredentialsUsernameAndDeletedFalse(username));
    }


    @Override
    public List<TweetResponseDto> getUserMentions(String username) {
        User user = userRepository.findByCredentialsUsername(username);
        validateUser(user);
        return tweetMapper.entitiesToResponseDtos(Sort.filterNotDeletedAndSortDesc(user.getTweetMentions()));
    }

    @Override
    public UserResponseDto getUser(String username) {
        User user = userRepository.findByCredentialsUsername(username);
        validateUser(user);
        return userMapper.entityToResponseDto(user);
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

    @Override
    public UserResponseDto updateUser(String username, UserRequestDto userRequestDto) {
        User user = userRepository.findByCredentialsUsername(username);
        validateUser(user);
        Validate.patchingProfile(userRequestDto);
        User updatedUser = userMapper.requestDtoToEntity(userRequestDto);

        if (!user.getCredentials().equals(updatedUser.getCredentials())) {
            throw new BadRequestException("The user's username or password doesn't match the user's given in the request body.");
        }

        Profile updatesToProfile = updatedUser.getProfile();

        if (updatesToProfile.getEmail() != null) {
            user.getProfile().setEmail(updatesToProfile.getEmail());
        }
        if (updatesToProfile.getPhone() != null) {
            user.getProfile().setPhone(updatesToProfile.getPhone());
        }
        if (updatesToProfile.getFirstName() != null) {
            user.getProfile().setFirstName(updatesToProfile.getFirstName());
        }
        if (updatesToProfile.getLastName() != null) {
            user.getProfile().setLastName(updatesToProfile.getLastName());
        }

        return userMapper.entityToResponseDto(userRepository.saveAndFlush(user));
    }

    public void unfollowUser(String username, CredentialsDto credentialsDto) {
        User userToUnfollow = userRepository.findByCredentialsUsername(username);
        User user = userRepository.findByCredentialsUsername(credentialsDto.getUsername());
        
        validateUser(user);
        validateUser(userToUnfollow);

        if(!user.getFollowing().contains(userToUnfollow) || !user.getCredentials().getPassword().equals(credentialsDto.getPassword())){
            throw new BadRequestException("The given user to follow currently isn't followed by the user," + 
            " or the given credentials are invalid");
        }

        user.getFollowing().remove(userToUnfollow);
        userToUnfollow.getFollowers().remove(user);

        userRepository.saveAndFlush(userToUnfollow);
        userRepository.saveAndFlush(user);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        if (user.getDeleted()) {
            throw new BadRequestException("User has been deleted");
        }
    }

    private boolean isUserCreatedAndNotDeleted(User user) {
        return user != null && !user.getDeleted();
    }

    private boolean isUserDeleted(User user) {
        return user != null && user.getDeleted();
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
