package com.cooksys.socialmedia.dtos;

import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@NoArgsConstructor
@Data
public class ContextDto {

    private TweetResponseDto target;

    private List<TweetResponseDto> before;

    private List<TweetResponseDto> after;
}
