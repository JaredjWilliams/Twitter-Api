package com.cooksys.socialmedia.utils;

import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.ProfileDto;
import com.cooksys.socialmedia.dtos.user.UserRequestDto;
import com.cooksys.socialmedia.entities.User;
import com.cooksys.socialmedia.exceptions.BadRequestException;

public class Validate {

    public static void validateCreatingAUser(UserRequestDto userRequestDto) {
        userRequestDto(userRequestDto);
        credentials(userRequestDto.getCredentials());
        profile(userRequestDto.getProfile());
        usernamePassword(userRequestDto.getCredentials());
        email(userRequestDto.getProfile());
        firstLastName(userRequestDto.getProfile());
        phone(userRequestDto.getProfile());
    }

    public static void patchingProfile(UserRequestDto userRequestDto) {
        Validate.userRequestDto(userRequestDto);
        Validate.credentials(userRequestDto.getCredentials());
        Validate.profile(userRequestDto.getProfile());
        Validate.usernamePassword(userRequestDto.getCredentials());
    }

    public static void userRequestDto(UserRequestDto userRequestDto) {
        if (userRequestDto == null) {
            throw new BadRequestException("UserRequestDto is null");
        }
    }

    public static void profile(ProfileDto profileDto) {
        if (profileDto == null) {
            throw new BadRequestException("ProfileDto is null");
        }
    }

    public static void credentials(CredentialsDto credentialsDto) {
        if (credentialsDto == null) {
            throw new BadRequestException("CredentialsDto is null");
        }
    }

    public static void usernamePassword(CredentialsDto credentialsDto) {
        if (credentialsDto.getUsername() == null) {
            throw new BadRequestException("Username is null");
        }
        if (credentialsDto.getPassword() == null) {
            throw new BadRequestException("Password is null");
        }
    }

    public static void email(ProfileDto profileDto) {
        if (profileDto.getEmail() == null) {
            throw new BadRequestException("Email is null");
        }
    }

    public static void firstLastName(ProfileDto profileDto) {
        if (profileDto.getFirstName() == null) {
            throw new BadRequestException("First name is null");
        }
        if (profileDto.getLastName() == null) {
            throw new BadRequestException("Last name is null");
        }
    }

    public static void phone(ProfileDto profileDto) {
        if (profileDto.getPhone() == null) {
            throw new BadRequestException("Phone is null");
        }
    }

    public static void user(User user) {
        if (user == null) {
            throw new BadRequestException("User is null");
        }
    }

}
