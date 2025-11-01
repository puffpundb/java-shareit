package ru.practicum.shareit.user.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {
	UserService userService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UserDto createUser(@RequestBody User user) {
		return userService.createUser(user);
	}

	@GetMapping("/{userId}")
	@ResponseStatus(HttpStatus.OK)
	public UserDto getUser(@PathVariable Long userId) {
		return userService.getUser(userId);
	}

	@PatchMapping("/{userId}")
	@ResponseStatus(HttpStatus.OK)
	public UserDto updateUserData(@PathVariable Long userId, @RequestBody User newUserData) {
		return userService.updateUser(userId, newUserData);
	}

	@DeleteMapping("/{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUser(@PathVariable Long userId) {
		userService.deleteUser(userId);
	}
}
