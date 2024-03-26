package com.cooksys.socialmedia.mappers;


import java.util.List;

import org.mapstruct.Mapper;

import com.cooksys.socialmedia.dtos.tweet.TweetRequestDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.entities.Tweet;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface TweetMapper {

    Tweet requestDtoToEntity(TweetRequestDto tweetDto);
    TweetResponseDto entityToResponseDto(Tweet tweet);
    List<Tweet> requestDtosToEntities(List<TweetRequestDto> tweetDtos);
    List<TweetResponseDto> entitiesToResponseDtos(List<Tweet> tweets);

}
