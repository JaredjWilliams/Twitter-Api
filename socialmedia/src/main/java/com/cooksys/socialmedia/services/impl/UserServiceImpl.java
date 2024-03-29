package com.cooksys.socialmedia.services.impl;

import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.ProfileDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.dtos.user.UserRequestDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;
import com.cooksys.socialmedia.entities.Profile;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;
import com.cooksys.socialmedia.exceptions.NotFoundException;
import com.cooksys.socialmedia.mappers.ProfileMapper;
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
    private final ProfileMapper profileMapper;

    private User getUserByCredentials(CredentialsDto credentialsDto) {
        return userRepository.findByCredentialsUsername(credentialsDto.getUsername());
    }

    @Override
    public List<UserResponseDto> getUsers() {
        return userMapper.entitiesToResponseDtos(userRepository.findByDeletedFalse());
    }

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        validatePatchingProfile(userRequestDto);
        User user = getUserByCredentials(userRequestDto.getCredentials());

        if (isUserCreatedAndNotDeleted(user)) {
            throw new BadRequestException("User can't be created because the user already exists");
        }
        validateProfile(userRequestDto);


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
    public UserResponseDto updateUser(String username, UserRequestDto userRequestDto) {
        User user = userRepository.findByCredentialsUsername(username);
        validateUser(user);
        validatePatchingProfile(userRequestDto);

        Profile profile = profileMapper.dtoToEntity(userRequestDto.getProfile());
        System.out.println(profile.getEmail());
        if (profile.getEmail() != null) {
            user.getProfile().setEmail(profile.getEmail());
        }
        user.getProfile().setPhone(profile.getPhone());
        user.getProfile().setFirstName(profile.getFirstName());
        user.getProfile().setLastName(profile.getLastName());

        return userMapper.entityToResponseDto(userRepository.saveAndFlush(user));

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

    private void validateProfile(UserRequestDto userRequestDto) {
        validateUserRequestDto(userRequestDto);
        CredentialsDto credentials = userRequestDto.getCredentials();

        validateCredentialObject(credentials);
        validateProfileObject(userRequestDto.getProfile());
        validateFirstLastName(userRequestDto.getProfile());
        validateEmail(userRequestDto.getProfile());
        validatePhoneNumber(userRequestDto.getProfile());
        validateUsernamePasswordPresent(credentials);
    }

    private void validatePatchingProfile(UserRequestDto userRequestDto) {
        validateUserRequestDto(userRequestDto);

        validateCredentialObject(userRequestDto.getCredentials());
        validateUsernamePasswordPresent(userRequestDto.getCredentials());
        validateProfileObject(userRequestDto.getProfile());

    }

    private static void validateUsernamePasswordPresent(CredentialsDto credentials) {
        if (credentials.getPassword() == null) throw new BadRequestException("Password is required");
        if (credentials.getUsername() == null) throw new BadRequestException("Username is required");
    }

    private static void validatePhoneNumber(ProfileDto profile) {
        if (profile.getPhone().isBlank()) throw new BadRequestException("Phone is required");
    }

    private static void validateFirstLastName(ProfileDto profile) {
        if (profile.getFirstName() == null) throw new BadRequestException("First name is required");
        if (profile.getLastName().isBlank()) throw new BadRequestException("Last name is required");
    }

    private static void validateEmail(ProfileDto profile) {
        if ((profile.getEmail() == null)) throw new BadRequestException("Email is required");
    }

    private static void validateProfileObject(ProfileDto profile) {
        if (profile == null) throw new BadRequestException("Profile is required");
    }

    private static void validateCredentialObject(CredentialsDto credentials) {
        if (credentials == null) throw new BadRequestException("Credentials are required");
    }

    private void validateUserRequestDto(UserRequestDto userRequestDto) {
        if (userRequestDto == null) throw new BadRequestException("User request is required");
    }


}
