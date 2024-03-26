package com.cooksys.socialmedia.mappers;

import com.cooksys.socialmedia.dtos.HashtagDto;
import com.cooksys.socialmedia.entities.Hashtag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HashtagMapper {

    Hashtag dtoToEntity(HashtagDto hashtagDto);
    HashtagDto entityToDto(Hashtag hashtag);
    List<Hashtag> dtosToEntities(List<HashtagDto> hashtagDtos);
    List<HashtagDto> entitiesToDtos(List<Hashtag> hashtags);

}
