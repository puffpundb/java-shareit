package ru.practicum.shareit.user.model.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor
public class UserMapper {
	public static UserDto toUserDto(User user) {
		UserDto userDto = new UserDto();
		userDto.setId(user.getId());
		userDto.setName(user.getName());
		userDto.setEmail(user.getEmail());

		return userDto;
	}
}
