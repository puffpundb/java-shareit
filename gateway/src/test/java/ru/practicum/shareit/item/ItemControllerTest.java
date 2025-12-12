package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ItemClient itemClient;

	@Autowired
	private ObjectMapper mapper;

	@Test
	void createItem() throws Exception {
		String requestJson = "{\"name\":\"Item\",\"description\":\"Desc\",\"available\":true}";
		String responseJson = "{\"id\":1,\"name\":\"Item\",\"description\":\"Desc\",\"available\":true,\"ownerId\":1}";

		when(itemClient.createItem(eq(1L), any(ItemDto.class)))
				.thenReturn(ResponseEntity.ok().body(responseJson.getBytes()));

		mockMvc.perform(post("/items")
						.header("X-Sharer-User-Id", 1)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Item"));
	}

	@Test
	void getItem() throws Exception {
		String responseJson = "{\"id\":1,\"name\":\"Item\",\"description\":\"Desc\",\"available\":true,\"ownerId\":1,\"lastBooking\":null,\"nextBooking\":null,\"comments\":[]}";

		when(itemClient.getItem(eq(1L), eq(1L)))
				.thenReturn(ResponseEntity.ok().body(responseJson.getBytes()));

		mockMvc.perform(get("/items/1")
						.header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Item"));
	}

	@Test
	void updateItem() throws Exception {
		String requestJson = "{\"name\":\"Updated\",\"description\":\"New desc\",\"available\":false}";
		String responseJson = "{\"id\":1,\"name\":\"Updated\",\"description\":\"New desc\",\"available\":false,\"ownerId\":1}";

		when(itemClient.updateItem(eq(1L), eq(1L), any(ItemDto.class)))
				.thenReturn(ResponseEntity.ok().body(responseJson.getBytes()));

		mockMvc.perform(patch("/items/1")
						.header("X-Sharer-User-Id", 1)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Updated"));
	}

	@Test
	void searchItems_emptyText() throws Exception {
		mockMvc.perform(get("/items/search")
						.header("X-Sharer-User-Id", 1)
						.param("search", ""))
				.andExpect(status().isOk())
				.andExpect(content().json("[]"));
	}

	@Test
	void searchItems() throws Exception {
		String responseJson = "[{\"id\":1,\"name\":\"Drill\",\"description\":\"Powerful\",\"available\":true,\"ownerId\":2}]";

		when(itemClient.searchItems(eq("drill"), eq(1L)))
				.thenReturn(ResponseEntity.ok().body(responseJson.getBytes()));

		mockMvc.perform(get("/items/search")
						.header("X-Sharer-User-Id", 1)
						.param("search", "drill"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value("Drill"));
	}

	@Test
	void getMyItems() throws Exception {
		String responseJson = "[{\"id\":1,\"name\":\"Item\",\"description\":\"Desc\",\"available\":true,\"ownerId\":1}]";

		when(itemClient.getMyItems(eq(1L)))
				.thenReturn(ResponseEntity.ok().body(responseJson.getBytes()));

		mockMvc.perform(get("/items")
						.header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value("Item"));
	}

	@Test
	void createComment() throws Exception {
		String requestJson = "{\"text\":\"Great!\"}";
		String responseJson = "{\"id\":1,\"text\":\"Great!\",\"authorName\":\"User\",\"created\":\"2025-12-08T10:00:00\"}";

		when(itemClient.createComment(eq(1L), eq(1L), any()))
				.thenReturn(ResponseEntity.ok().body(responseJson.getBytes()));

		mockMvc.perform(post("/items/1/comment")
						.header("X-Sharer-User-Id", 1)
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.text").value("Great!"));
	}
}
