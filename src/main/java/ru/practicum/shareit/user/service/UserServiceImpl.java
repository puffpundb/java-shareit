package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserDtoUpdate;
import ru.practicum.shareit.user.model.mapper.UserMapper;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
	final UserRepository userDb;

	@Override
	public UserDto createUser(UserDto user) {
		log.info("UserService: Создание пользователя. user: {}", user);
		User newUser = UserMapper.toUser(user);
		userDb.existsByEmailIgnoreCase(user.getEmail());
		userDb.save(newUser);

		return UserMapper.toUserDto(userDb.putUser(newUser));
	}

	@Override
	public UserDto updateUser(Long id, UserDtoUpdate newUserData) {
		log.info("UserService: Обновление пользователя. newUserData: {}", newUserData);

		User dbUser = userDb.getUser(id).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id: %d не найден", id)));
		if (userDb.emailExist(UserMapper.toUser(newUserData))) throw new ValidationException("Пользователь с данным email уже существует");

		log.info("UserService: Старый пользователь. dbUser: {}", dbUser);
		if (newUserData.getName() != null) dbUser.setName(newUserData.getName());
		if (newUserData.getEmail() != null && !newUserData.getEmail().equals(dbUser.getEmail())) dbUser.setEmail(newUserData.getEmail());
		log.info("UserService: Обновленный пользователь. newUserData: {} \n", dbUser);

		return UserMapper.toUserDto(userDb.putUser(dbUser));
	}

	@Override
	public UserDto getUser(Long id) {
		log.info("UserService: Получение пользователя. id: {}", id);
		User dbUser = userDb.getUser(id).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id: %d не найден", id)));

		return UserMapper.toUserDto(dbUser);
	}

	@Override
	public void deleteUser(Long id) {
		log.info("UserService: Удаление пользователя. id: {}", id);
		if (userDb.deleteUser(id) == null) throw new NotFoundException(String.format("Пользователь с id: %d не найден", id));
	}
}
