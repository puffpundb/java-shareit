package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserClient userClient;

	@Autowired
	private ObjectMapper mapper;

	@Test
	void createUser() throws Exception {
		String requestJson = """
        {"name":"User","email":"user@example.com"}
        """;
		String responseJson = """
        {"id":1,"name":"User","email":"user@example.com"}
        """;

		when(userClient.createUser(any(UserDto.class)))
				.thenReturn(ResponseEntity.ok().body(responseJson.getBytes()));

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("User"));
	}

	@Test
	void getUser() throws Exception {
		String jsonResponse = """
        {"id":1,"name":"User","email":"user@example.com"}
        """;

		when(userClient.getUser(1L))
				.thenReturn(ResponseEntity.ok().body(jsonResponse.getBytes()));

		mockMvc.perform(get("/users/1")
						.header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("User"));
	}

	@Test
	void updateUser() throws Exception {
		UserDtoUpdate request = new UserDtoUpdate();
		request.setName("Updated");
		request.setEmail("updated@example.com");

		String jsonResponse = """
        {"id":1,"name":"Updated","email":"updated@example.com"}
        """;
		when(userClient.updateUser(1L, request))
				.thenReturn(ResponseEntity.ok().body(jsonResponse.getBytes()));

		mockMvc.perform(patch("/users/1")
						.header("X-Sharer-User-Id", 1)
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Updated"));
	}

	@Test
	void deleteUser() throws Exception {
		mockMvc.perform(delete("/users/1")
						.header("X-Sharer-User-Id", 1))
				.andExpect(status().isNoContent());
	}
}
