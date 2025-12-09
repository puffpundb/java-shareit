package ru.practicum.shareit.request.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.model.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequestWithoutItemsDto;
import ru.practicum.shareit.request.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.controller.ItemController.USER_ID_HEADER;

@ExtendWith(MockitoExtension.class)
class RequestControllerTest {

	@Mock
	RequestService requestService;

	@InjectMocks
	RequestController requestController;

	private MockMvc mockMvc() {
		return MockMvcBuilders.standaloneSetup(requestController).build();
	}

	@Test
	void createRequest() throws Exception {
		MockMvc mvc = mockMvc();
		Long userId = 1L;

		ItemRequestWithoutItemsDto response = new ItemRequestWithoutItemsDto();
		response.setId(10L);
		response.setDescription("Need item");
		response.setCreated(LocalDateTime.of(2030, 1, 1, 10, 0));

		when(requestService.createRequest(eq(userId), any(ItemRequestWithoutItemsDto.class)))
				.thenReturn(response);

		String json = """
                {
                  "description": "Need item"
                }
                """;

		mvc.perform(post("/requests")
						.header(USER_ID_HEADER, userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(10)))
				.andExpect(jsonPath("$.description", is("Need item")));
	}

	@Test
	void getOwnRequests() throws Exception {
		MockMvc mvc = mockMvc();
		Long userId = 1L;

		ItemRequestWithItemsDto dto1 = new ItemRequestWithItemsDto();
		dto1.setId(10L);
		dto1.setDescription("Req1");

		ItemRequestWithItemsDto dto2 = new ItemRequestWithItemsDto();
		dto2.setId(20L);
		dto2.setDescription("Req2");

		when(requestService.getOwnRequests(userId)).thenReturn(List.of(dto1, dto2));

		mvc.perform(get("/requests")
						.header(USER_ID_HEADER, userId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(10)))
				.andExpect(jsonPath("$[1].description", is("Req2")));
	}

	@Test
	void getAllRequests() throws Exception {
		MockMvc mvc = mockMvc();
		Long userId = 1L;

		ItemRequestWithoutItemsDto dto = new ItemRequestWithoutItemsDto();
		dto.setId(30L);
		dto.setDescription("Other req");

		when(requestService.getOtherRequest(userId)).thenReturn(List.of(dto));

		mvc.perform(get("/requests/all")
						.header(USER_ID_HEADER, userId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(30)))
				.andExpect(jsonPath("$[0].description", is("Other req")));
	}

	@Test
	void getRequestById() throws Exception {
		MockMvc mvc = mockMvc();
		Long requestId = 10L;

		ItemRequestWithItemsDto dto = new ItemRequestWithItemsDto();
		dto.setId(requestId);
		dto.setDescription("Req");

		when(requestService.getRequestById(requestId)).thenReturn(dto);

		mvc.perform(get("/requests/{requestId}", requestId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(10)))
				.andExpect(jsonPath("$.description", is("Req")));
	}
}
