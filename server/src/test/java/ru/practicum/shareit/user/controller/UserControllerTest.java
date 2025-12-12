package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserDtoUpdate;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

	@MockBean
	UserService userService;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void createUser() throws Exception {
		UserDto response = new UserDto();
		response.setId(1L);
		response.setName("User");
		response.setEmail("user@test.com");

		when(userService.createUser(any(UserDto.class))).thenReturn(response);

		UserDto request = new UserDto();
		request.setName("User");
		request.setEmail("user@test.com");

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("User")))
				.andExpect(jsonPath("$.email", is("user@test.com")));
	}

	@Test
	void getUser() throws Exception {
		Long userId = 1L;

		UserDto response = new UserDto();
		response.setId(userId);
		response.setName("User");
		response.setEmail("user@test.com");

		when(userService.getUser(userId)).thenReturn(response);

		mockMvc.perform(get("/users/{userId}", userId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.email", is("user@test.com")));
	}

	@Test
	void updateUserData() throws Exception {
		Long userId = 1L;

		UserDtoUpdate update = new UserDtoUpdate();
		update.setName("New");
		update.setEmail("new@test.com");

		UserDto response = new UserDto();
		response.setId(userId);
		response.setName("New");
		response.setEmail("new@test.com");

		when(userService.updateUser(eq(userId), any(UserDtoUpdate.class))).thenReturn(response);

		mockMvc.perform(patch("/users/{userId}", userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(update)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("New")))
				.andExpect(jsonPath("$.email", is("new@test.com")));
	}

	@Test
	void deleteUser() throws Exception {
		Long userId = 1L;

		doNothing().when(userService).deleteUser(userId);

		mockMvc.perform(delete("/users/{userId}", userId))
				.andExpect(status().isNoContent());
	}
}
