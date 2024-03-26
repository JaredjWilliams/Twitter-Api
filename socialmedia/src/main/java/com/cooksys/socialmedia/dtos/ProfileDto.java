package com.cooksys.socialmedia.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@Data
public class ProfileDto {
    private String firstName;

    private String lastName;

    @NonNull
    private String email;

    private String phone;
}
