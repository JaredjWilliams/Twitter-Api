package com.cooksys.socialmedia.dtos;

import java.sql.Timestamp;

import com.cooksys.socialmedia.entities.Credentials;
import com.cooksys.socialmedia.entities.Profile;

import jakarta.persistence.Embedded;

public class UserRequestDto {

    @Embedded
    private Credentials credentials;

    @Embedded
    private Profile profile;


}
