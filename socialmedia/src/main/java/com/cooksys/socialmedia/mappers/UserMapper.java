package com.cooksys.socialmedia.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cooksys.socialmedia.dtos.user.UserRequestDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;
import com.cooksys.socialmedia.entities.User;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class, CredentialsMapper.class})

public interface UserMapper {
    @Mapping(target = "username", source = "credentials.username")
	UserResponseDto entityToResponseDto(User user);
    User requestDtoToEntity(UserRequestDto userDto);
    List<User> requestDtosToEntities(List<UserRequestDto> userDtos);
    List<UserResponseDto> entitiesToResponseDtos(List<User> users);

}
