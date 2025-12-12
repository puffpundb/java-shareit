package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserDtoUpdate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	UserRepository userDb;

	@InjectMocks
	UserServiceImpl userService;

	@Test
	void createUser() {
		UserDto dto = new UserDto();
		dto.setName("User");
		dto.setEmail("user@test.com");

		User saved = new User();
		saved.setId(1L);
		saved.setName("User");
		saved.setEmail("user@test.com");

		when(userDb.existsByEmailIgnoreCase(dto.getEmail())).thenReturn(false);
		when(userDb.save(any(User.class))).thenReturn(saved);

		UserDto result = userService.createUser(dto);

		assertNotNull(result);
		assertEquals(1L, result.getId());
		assertEquals("user@test.com", result.getEmail());
		verify(userDb).save(any(User.class));
	}

	@Test
	void createUser_emailExists() {
		UserDto dto = new UserDto();
		dto.setName("User");
		dto.setEmail("user@test.com");

		when(userDb.existsByEmailIgnoreCase(dto.getEmail())).thenReturn(true);

		assertThrows(ExistException.class, () -> userService.createUser(dto));
		verify(userDb, never()).save(any());
	}

	@Test
	void updateUser() {
		Long id = 1L;

		User existing = new User();
		existing.setId(id);
		existing.setName("Old");
		existing.setEmail("old@test.com");

		UserDtoUpdate update = new UserDtoUpdate();
		update.setName("New");
		update.setEmail("new@test.com");

		User saved = new User();
		saved.setId(id);
		saved.setName("New");
		saved.setEmail("new@test.com");

		when(userDb.findById(id)).thenReturn(Optional.of(existing));
		when(userDb.existsByEmailIgnoreCaseAndIdNot(update.getEmail(), id)).thenReturn(false);
		when(userDb.save(existing)).thenReturn(saved);

		UserDto result = userService.updateUser(id, update);

		assertNotNull(result);
		assertEquals("New", result.getName());
		assertEquals("new@test.com", result.getEmail());
		verify(userDb).save(existing);
	}

	@Test
	void updateUser_emailAlreadyUsed() {
		Long id = 1L;

		User existing = new User();
		existing.setId(id);
		existing.setName("Old");
		existing.setEmail("old@test.com");

		UserDtoUpdate update = new UserDtoUpdate();
		update.setEmail("other@test.com");

		when(userDb.findById(id)).thenReturn(Optional.of(existing));
		when(userDb.existsByEmailIgnoreCaseAndIdNot(update.getEmail(), id)).thenReturn(true);

		assertThrows(ExistException.class, () -> userService.updateUser(id, update));
		verify(userDb, never()).save(any());
	}

	@Test
	void updateUser_notFound() {
		Long id = 1L;
		UserDtoUpdate update = new UserDtoUpdate();
		update.setName("New");

		when(userDb.findById(id)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> userService.updateUser(id, update));
	}

	@Test
	void getUser() {
		Long id = 1L;

		User user = new User();
		user.setId(id);
		user.setName("User");
		user.setEmail("user@test.com");

		when(userDb.findById(id)).thenReturn(Optional.of(user));

		UserDto result = userService.getUser(id);

		assertNotNull(result);
		assertEquals(id, result.getId());
		assertEquals("user@test.com", result.getEmail());
	}

	@Test
	void getUser_notFound() {
		Long id = 1L;

		when(userDb.findById(id)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> userService.getUser(id));
	}

	@Test
	void deleteUser() {
		Long id = 1L;

		when(userDb.existsById(id)).thenReturn(true);

		userService.deleteUser(id);

		verify(userDb).deleteById(id);
	}

	@Test
	void deleteUser_notFound() {
		Long id = 1L;

		when(userDb.existsById(id)).thenReturn(false);

		assertThrows(NotFoundException.class, () -> userService.deleteUser(id));
		verify(userDb, never()).deleteById(anyLong());
	}

	@Test
	void checkAndGetUser() {
		Long id = 1L;

		User user = new User();
		user.setId(id);

		when(userDb.findById(id)).thenReturn(Optional.of(user));

		User result = userService.checkAndGetUser(id);

		assertEquals(id, result.getId());
	}

	@Test
	void checkAndGetUser_notFound() {
		Long id = 1L;

		when(userDb.findById(id)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> userService.checkAndGetUser(id));
	}
}
