package ru.practicum.shareit.user.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	@Mock
	UserService userService;

	@InjectMocks
	UserController userController;

	private MockMvc mockMvc() {
		return MockMvcBuilders.standaloneSetup(userController).build();
	}

	@Test
	void createUser() throws Exception {
		MockMvc mvc = mockMvc();

		UserDto response = new UserDto();
		response.setId(1L);
		response.setName("User");
		response.setEmail("user@test.com");

		when(userService.createUser(any(UserDto.class))).thenReturn(response);

		String json = "{\"name\":\"User\",\"email\":\"user@test.com\"}";

		mvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("User")))
				.andExpect(jsonPath("$.email", is("user@test.com")));
	}

	@Test
	void getUser() throws Exception {
		MockMvc mvc = mockMvc();
		Long userId = 1L;

		UserDto response = new UserDto();
		response.setId(userId);
		response.setName("User");
		response.setEmail("user@test.com");

		when(userService.getUser(userId)).thenReturn(response);

		mvc.perform(get("/users/{userId}", userId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.email", is("user@test.com")));
	}

	@Test
	void updateUserData() throws Exception {
		MockMvc mvc = mockMvc();
		Long userId = 1L;

		UserDtoUpdate update = new UserDtoUpdate();
		update.setName("New");
		update.setEmail("new@test.com");

		UserDto response = new UserDto();
		response.setId(userId);
		response.setName("New");
		response.setEmail("new@test.com");

		when(userService.updateUser(eq(userId), any(UserDtoUpdate.class))).thenReturn(response);

		String json = "{\"name\":\"New\",\"email\":\"new@test.com\"}";

		mvc.perform(patch("/users/{userId}", userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("New")))
				.andExpect(jsonPath("$.email", is("new@test.com")));
	}

	@Test
	void deleteUser() throws Exception {
		MockMvc mvc = mockMvc();
		Long userId = 1L;

		doNothing().when(userService).deleteUser(userId);

		mvc.perform(delete("/users/{userId}", userId))
				.andExpect(status().isNoContent());
	}
}
