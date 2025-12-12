package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestWithoutItemsDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
class RequestControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RequestClient requestClient;

	@Autowired
	private ObjectMapper mapper;

	@Test
	void createRequest() throws Exception {
		String requestJson = "{\"description\":\"Need a drill\"}";
		String responseJson = "{\"id\":1,\"description\":\"Need a drill\",\"created\":\"2025-12-08T12:00:00\"}";

		when(requestClient.createRequest(eq(1L), any(ItemRequestWithoutItemsDto.class)))
				.thenReturn(ResponseEntity.ok().body(responseJson.getBytes()));

		mockMvc.perform(post("/requests")
						.header("X-Sharer-User-Id", 1)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.description").value("Need a drill"));
	}

	@Test
	void getOwnRequests() throws Exception {
		String responseJson = "[{\"id\":1,\"description\":\"Need a drill\",\"created\":\"2025-12-08T12:00:00\",\"items\":[]}]";

		when(requestClient.getOwnRequest(eq(1L)))
				.thenReturn(ResponseEntity.ok().body(responseJson.getBytes()));

		mockMvc.perform(get("/requests")
						.header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].description").value("Need a drill"));
	}

	@Test
	void getAllRequest() throws Exception {
		String responseJson = "[{\"id\":2,\"description\":\"Need a saw\",\"created\":\"2025-12-08T12:00:00\"}]";

		when(requestClient.getAllRequest(eq(1L)))
				.thenReturn(ResponseEntity.ok().body(responseJson.getBytes()));

		mockMvc.perform(get("/requests/all")
						.header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].description").value("Need a saw"));
	}

	@Test
	void getRequestById() throws Exception {
		String responseJson = "{\"id\":1,\"description\":\"Need a drill\",\"created\":\"2025-12-08T12:00:00\",\"items\":[]}";

		when(requestClient.getRequestById(eq(1L)))
				.thenReturn(ResponseEntity.ok().body(responseJson.getBytes()));

		mockMvc.perform(get("/requests/1")
						.header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.description").value("Need a drill"));
	}
}
