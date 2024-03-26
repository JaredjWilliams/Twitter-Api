package com.cooksys.socialmedia.mappers;

import com.cooksys.socialmedia.dtos.user.UserRequestDto;
import com.cooksys.socialmedia.dtos.user.UserResponseDto;
import com.cooksys.socialmedia.entities.User;

import java.util.List;

public interface UserMapper {

    User requestDtoToEntity(UserRequestDto userDto);
    UserResponseDto entityToResponseDto(User user);
    List<User> requestDtosToEntities(List<UserRequestDto> userDtos);
    List<UserResponseDto> entitiesToResponseDtos(List<User> users);

}
