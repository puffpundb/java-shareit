package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
	private final UserClient userClient;

	@PostMapping
	public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {

		return userClient.createUser(userDto);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<Object> getUser(@PathVariable(name = "userId") long userId) {

		return userClient.getUser(userId);
	}

	@PatchMapping("/{userId}")
	public ResponseEntity<Object> updateUser(@PathVariable(name = "userId") long userId, @Valid @RequestBody UserDtoUpdate newUserData) {

		return userClient.updateUser(userId, newUserData);
	}

	@DeleteMapping("/{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<Object> deleteUser(@PathVariable(name = "userId") long userId) {

		return userClient.deleteUser(userId);
	}
}
