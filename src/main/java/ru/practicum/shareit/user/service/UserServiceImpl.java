package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.UserDal;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
	final UserDal userDb;

	@Override
	public UserDto createUser(User user) {
		UserValidator.validateUserToCreate(user, userDb);

		return UserMapper.toUserDto(userDb.createUser(user));
	}

	@Override
	public UserDto updateUser(Long id, User newUserData) {
		newUserData.setId(id);
		UserValidator.validateUserToUpdate(newUserData, userDb);

		return UserMapper.toUserDto(userDb.updateUser(newUserData));
	}

	@Override
	public UserDto getUser(Long id) {
		Optional<User> optionalUserFromDb = userDb.getUser(id);
		if (optionalUserFromDb.isEmpty()) throw new NotFoundException(String.format("Пользователь с id: %d не найден", id));

		return UserMapper.toUserDto(optionalUserFromDb.get());
	}

	@Override
	public void deleteUser(Long id) {
		if (userDb.deleteUser(id) == null) throw new NotFoundException(String.format("Пользователь с id: %d не найден", id));
	}
}
