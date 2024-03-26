package com.cooksys.socialmedia.dtos;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CredentialsDto {

    private String username;
    private String password;

}
