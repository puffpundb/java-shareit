package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

public interface UserService {
	UserDto createUser(User user);
	UserDto updateUser(Long id, User user);
	UserDto getUser(Long id);
	void deleteUser(Long id);
}
