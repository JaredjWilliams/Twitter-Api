package com.cooksys.socialmedia.dtos;

import com.cooksys.socialmedia.entities.Credentials;
import com.cooksys.socialmedia.entities.Profile;

import jakarta.persistence.Embedded;

public class UserResponseDto {

    private CredentialsDto credentialsDto; 

    private ProfileDto profileDto;

}
