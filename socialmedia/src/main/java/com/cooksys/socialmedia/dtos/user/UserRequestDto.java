package com.cooksys.socialmedia.dtos.user;

import java.sql.Timestamp;

import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.dtos.ProfileDto;
import com.cooksys.socialmedia.entities.Credentials;
import com.cooksys.socialmedia.entities.Profile;

import jakarta.persistence.Embedded;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserRequestDto {

    @Embedded
    private CredentialsDto credentials;

    @Embedded
    private ProfileDto profile;

}
