package com.cooksys.socialmedia.dtos.tweet;

import com.cooksys.socialmedia.dtos.CredentialsDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TweetRequestDto {
    private String content;
    private CredentialsDto credentials;
}
