package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserDtoUpdate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceImplIntegrationTest {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Test
	void createUserSuccess() {
		UserDto dto = new UserDto();
		dto.setName("User");
		dto.setEmail("user@test.com");

		UserDto result = userService.createUser(dto);

		assertThat(result.getId()).isNotNull();
		assertThat(result.getName()).isEqualTo("User");
		assertThat(result.getEmail()).isEqualTo("user@test.com");

		User fromDb = userRepository.findById(result.getId()).orElseThrow();
		assertThat(fromDb.getName()).isEqualTo("User");
		assertThat(fromDb.getEmail()).isEqualTo("user@test.com");
	}

	@Test
	void createUserThrowsWhenEmailExists() {
		User existing = new User();
		existing.setName("Existing");
		existing.setEmail("dup@test.com");
		existing = userRepository.save(existing);

		UserDto dto = new UserDto();
		dto.setName("Another");
		dto.setEmail("dup@test.com");

		assertThatThrownBy(() -> userService.createUser(dto))
				.isInstanceOf(ExistException.class)
				.hasMessage("Данный email уже используется");
	}

	@Test
	void updateUserChangesNameAndEmail() {
		User user = new User();
		user.setName("Old");
		user.setEmail("old@test.com");
		user = userRepository.save(user);

		UserDtoUpdate update = new UserDtoUpdate();
		update.setName("New");
		update.setEmail("new@test.com");

		UserDto result = userService.updateUser(user.getId(), update);

		assertThat(result.getName()).isEqualTo("New");
		assertThat(result.getEmail()).isEqualTo("new@test.com");

		User fromDb = userRepository.findById(user.getId()).orElseThrow();
		assertThat(fromDb.getName()).isEqualTo("New");
		assertThat(fromDb.getEmail()).isEqualTo("new@test.com");
	}

	@Test
	void updateUserThrowsWhenEmailUsedByAnother() {
		User user1 = new User();
		user1.setName("User1");
		user1.setEmail("user1@test.com");
		user1 = userRepository.save(user1);

		User user2 = new User();
		user2.setName("User2");
		user2.setEmail("user2@test.com");
		user2 = userRepository.save(user2);

		UserDtoUpdate update = new UserDtoUpdate();
		update.setEmail("user1@test.com");

		Long targetId = user2.getId();

		assertThatThrownBy(() -> userService.updateUser(targetId, update))
				.isInstanceOf(ExistException.class)
				.hasMessage("Данный email уже используется");
	}

	@Test
	void deleteUserRemovesUser() {
		User user = new User();
		user.setName("ToDelete");
		user.setEmail("todelete@test.com");
		user = userRepository.save(user);

		Long id = user.getId();

		userService.deleteUser(id);

		assertThat(userRepository.existsById(id)).isFalse();
	}

	@Test
	void deleteUserThrowsWhenNotFound() {
		Long id = 999L;

		assertThatThrownBy(() -> userService.deleteUser(id))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Пользователь с id: %d не найден".formatted(id));
	}
}
