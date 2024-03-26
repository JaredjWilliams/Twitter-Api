package com.cooksys.socialmedia.dtos.user;

import com.cooksys.socialmedia.dtos.ProfileDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
public class UserResponseDto {

    @NonNull
    private String username;

    @NonNull
    private ProfileDto profile;

    @NonNull
    private Timestamp joined;

}
