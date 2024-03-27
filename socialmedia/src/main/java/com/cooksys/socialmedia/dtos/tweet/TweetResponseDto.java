package com.cooksys.socialmedia.dtos.tweet;

import com.cooksys.socialmedia.dtos.user.UserResponseDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
public class TweetResponseDto {

    @NonNull
    private Long id;

    @NonNull
    private UserResponseDto author;

    @NonNull
    private Timestamp posted;

    private String content;

    private TweetResponseDto inReplyTo;

    private TweetResponseDto repostOf;
}
