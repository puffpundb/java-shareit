package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.UserDal;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.mapper.UserMapper;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
	final UserDal userDb;
	Long currentMaxId = 0L; // Это явно должно быть на уровне бд, но я сюда вынес, чтобы удобнее было логировать

	@Override
	public UserDto createUser(User user) {
		log.info("Валидация пользователя. user: {}", user);
		user.setId(currentMaxId++);
		UserValidator.validateUserToCreate(user, userDb);

		return UserMapper.toUserDto(userDb.putUser(user));
	}

	@Override
	public UserDto updateUser(Long id, User newUserData) {
		newUserData.setId(id);
		log.info("Проверка существования пользователя. ownerId: {}", id);
		UserValidator.validateUserToUpdate(newUserData, userDb);

		Optional<User> optionalUser = userDb.getUser(id);
		if (optionalUser.isEmpty()) throw new NotFoundException(String.format("Пользователь с id: %d не найден", id));

		User dbUser = optionalUser.get();
		log.info("Старый пользователь. dbUser: {}", dbUser);
		if (newUserData.getName() != null) dbUser.setName(newUserData.getName());
		if (newUserData.getEmail() != null) dbUser.setEmail(newUserData.getEmail());
		log.info("Обновленный пользователь. newUserData: {} \n", dbUser);

		return UserMapper.toUserDto(userDb.putUser(newUserData));
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
