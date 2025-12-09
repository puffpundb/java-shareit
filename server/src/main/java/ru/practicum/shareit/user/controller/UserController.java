package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserDtoUpdate;
import ru.practicum.shareit.user.service.UserService;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {
	UserService userService;

	@PostMapping //ready
	@ResponseStatus(HttpStatus.CREATED)
	public UserDto createUser(@Valid @RequestBody UserDto newUser) {
		log.info("UserController: Запрос на создание нового пользователя");

		return userService.createUser(newUser);
	}

	@GetMapping("/{userId}") //ready
	@ResponseStatus(HttpStatus.OK)
	public UserDto getUser(@PathVariable Long userId) {
		log.info("UserController: Запрос на получение пользователя");

		return userService.getUser(userId);
	}

	@PatchMapping("/{userId}") //ready
	@ResponseStatus(HttpStatus.OK)
	public UserDto updateUserData(@PathVariable Long userId, @Valid @RequestBody UserDtoUpdate newUserData) {
		log.info("UserController: Запрос на обновление пользователя");

		return userService.updateUser(userId, newUserData);
	}

	@DeleteMapping("/{userId}") //ready
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUser(@PathVariable Long userId) {
		log.info("UserController: Запрос на удаление пользователя");

		userService.deleteUser(userId);
	}
}
