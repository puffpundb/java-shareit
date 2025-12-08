package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ExistException;
import ru.practicum.shareit.exception.NotFoundException;
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
	@Transactional
	public UserDto createUser(UserDto user) {
		log.info("UserService: Создание пользователя. user: {}", user);
		if (userDb.existsByEmailIgnoreCase(user.getEmail())) throw new ExistException("Данный email уже используется");
		User newUser = UserMapper.toUser(user);

		return UserMapper.toUserDto(userDb.save(newUser));
	}

	@Override
	@Transactional
	public UserDto updateUser(Long id, UserDtoUpdate newUserData) {
		log.info("UserService: Обновление пользователя. newUserData: {}", newUserData);

		User dbUser = userDb.findById(id).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id: %d не найден", id)));

		log.info("UserService: Старый пользователь. dbUser: {}", dbUser);
		if (newUserData.getName() != null) dbUser.setName(newUserData.getName());
		if (newUserData.getEmail() != null && !newUserData.getEmail().equals(dbUser.getEmail())) {
			if (userDb.existsByEmailIgnoreCaseAndIdNot(newUserData.getEmail(), id)) throw new ExistException("Данный email уже используется");
			dbUser.setEmail(newUserData.getEmail());
		}
		log.info("UserService: Обновленный пользователь. newUserData: {} \n", dbUser);

		return UserMapper.toUserDto(userDb.save(dbUser));
	}

	@Override
	@Transactional
	public UserDto getUser(Long id) {
		log.info("UserService: Получение пользователя. id: {}", id);
		User dbUser = userDb.findById(id).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id: %d не найден", id)));

		return UserMapper.toUserDto(dbUser);
	}

	@Override
	@Transactional
	public void deleteUser(Long id) {
		log.info("UserService: Удаление пользователя. id: {}", id);
		if (!userDb.existsById(id)) throw new NotFoundException(String.format("Пользователь с id: %d не найден", id));

		userDb.deleteById(id);
	}

	@Override
	public User checkAndGetUser(Long id) {
		log.info("ItemService: Получение пользователя с id: {}", id);
		return userDb.findById(id).orElseThrow(() ->
				new NotFoundException(String.format("Пользователь с данным id: %d не найден", id)));
	}
}
