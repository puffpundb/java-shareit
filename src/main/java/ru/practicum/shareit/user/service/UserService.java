package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserDtoUpdate;

public interface UserService {
	UserDto createUser(UserDto user);

	UserDto updateUser(Long id, UserDtoUpdate user);

	UserDto getUser(Long id);

	void deleteUser(Long id);
}
