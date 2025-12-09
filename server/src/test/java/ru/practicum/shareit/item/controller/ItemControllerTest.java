package ru.practicum.shareit.item.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.CreateCommentRequest;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

	@Mock
	ItemService itemService;

	@InjectMocks
	ItemController itemController;

	private MockMvc mockMvc() {
		return MockMvcBuilders.standaloneSetup(itemController).build();
	}

	@Test
	void createItem() throws Exception {
		MockMvc mvc = mockMvc();
		Long userId = 1L;

		ItemDto response = new ItemDto();
		response.setId(10L);
		response.setName("Item");
		response.setDescription("Desc");
		response.setAvailable(true);

		when(itemService.createItem(eq(userId), any(ItemDto.class))).thenReturn(response);

		String json = """
                {
                  "name": "Item",
                  "description": "Desc",
                  "available": true
                }
                """;

		mvc.perform(post("/items")
						.header(ItemController.USER_ID_HEADER, userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(10)))
				.andExpect(jsonPath("$.name", is("Item")))
				.andExpect(jsonPath("$.available", is(true)));
	}

	@Test
	void updateItem() throws Exception {
		MockMvc mvc = mockMvc();
		Long ownerId = 1L;
		Long itemId = 5L;

		ItemDto response = new ItemDto();
		response.setId(itemId);
		response.setName("Updated");
		response.setDescription("New desc");
		response.setAvailable(false);

		when(itemService.updateItem(eq(ownerId), eq(itemId), any(ItemDto.class))).thenReturn(response);

		String json = """
                {
                  "name": "Updated",
                  "description": "New desc",
                  "available": false
                }
                """;

		mvc.perform(patch("/items/{itemId}", itemId)
						.header(ItemController.USER_ID_HEADER, ownerId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(5)))
				.andExpect(jsonPath("$.name", is("Updated")))
				.andExpect(jsonPath("$.available", is(false)));
	}

	@Test
	void getItem() throws Exception {
		MockMvc mvc = mockMvc();
		Long userId = 2L;
		Long itemId = 10L;

		ItemDto response = new ItemDto();
		response.setId(itemId);
		response.setName("Item");
		response.setDescription("Desc");
		response.setAvailable(true);

		when(itemService.getItemById(itemId, userId)).thenReturn(response);

		mvc.perform(get("/items/{itemId}", itemId)
						.header(ItemController.USER_ID_HEADER, userId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(10)))
				.andExpect(jsonPath("$.name", is("Item")));
	}

	@Test
	void getMyItems() throws Exception {
		MockMvc mvc = mockMvc();
		Long ownerId = 1L;

		ItemDto dto1 = new ItemDto();
		dto1.setId(1L);
		dto1.setName("Item1");

		ItemDto dto2 = new ItemDto();
		dto2.setId(2L);
		dto2.setName("Item2");

		when(itemService.getOwnerItems(ownerId)).thenReturn(List.of(dto1, dto2));

		mvc.perform(get("/items")
						.header(ItemController.USER_ID_HEADER, ownerId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[1].name", is("Item2")));
	}

	@Test
	void searchItems() throws Exception {
		MockMvc mvc = mockMvc();
		String text = "it";

		ItemDto dto = new ItemDto();
		dto.setId(3L);
		dto.setName("Item3");

		when(itemService.getSearch(text)).thenReturn(List.of(dto));

		mvc.perform(get("/items/search")
						.param("text", text))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(3)))
				.andExpect(jsonPath("$[0].name", is("Item3")));
	}

	@Test
	void createComment() throws Exception {
		MockMvc mvc = mockMvc();
		Long userId = 1L;
		Long itemId = 10L;

		CommentDto response = new CommentDto();
		response.setId(100L);
		response.setText("Nice");
		response.setAuthorName("User");
		response.setCreated(LocalDateTime.of(2030, 1, 1, 10, 0));

		when(itemService.createComment(eq(userId), eq(itemId), any(CreateCommentRequest.class)))
				.thenReturn(response);

		String json = """
                {
                  "text": "Nice"
                }
                """;

		mvc.perform(post("/items/{itemId}/comment", itemId)
						.header(ItemController.USER_ID_HEADER, userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(100)))
				.andExpect(jsonPath("$.text", is("Nice")))
				.andExpect(jsonPath("$.authorName", is("User")));
	}
}
