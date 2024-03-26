package com.cooksys.socialmedia.mappers;


import com.cooksys.socialmedia.dtos.tweet.TweetRequestDto;
import com.cooksys.socialmedia.dtos.tweet.TweetResponseDto;
import com.cooksys.socialmedia.entities.Tweet;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = ProfileMapper.class)
public interface TweetMapper {

    Tweet requestDtoToEntity(TweetRequestDto tweetDto);
    TweetResponseDto entityToResponseDto(Tweet tweet);
    List<Tweet> requestDtosToEntities(List<TweetRequestDto> tweetDtos);
    List<TweetResponseDto> entitiesToResponseDtos(List<Tweet> tweets);

}
