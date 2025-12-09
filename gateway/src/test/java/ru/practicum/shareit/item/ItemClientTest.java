package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.item.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemClientTest {

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private RestTemplateBuilder restTemplateBuilder;

	private ItemClient itemClient;

	@BeforeEach
	void setUp() {
		RestTemplateBuilder chainedBuilder = mock(RestTemplateBuilder.class);
		when(restTemplateBuilder.uriTemplateHandler(any(DefaultUriBuilderFactory.class)))
				.thenReturn(chainedBuilder);
		when(chainedBuilder.requestFactory(any(Supplier.class)))  // ← ТОЧНЫЙ тип Supplier
				.thenReturn(chainedBuilder);
		when(chainedBuilder.build()).thenReturn(restTemplate);

		itemClient = new ItemClient("http://localhost:9999", restTemplateBuilder);
	}

	@Test
	void createItem() {
		long userId = 1L;
		ItemDto itemDto = new ItemDto();
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

		when(restTemplate.exchange(eq(""), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
				.thenReturn(expectedResponse);

		ResponseEntity<Object> result = itemClient.createItem(userId, itemDto);

		assertEquals(HttpStatus.OK, result.getStatusCode());
		verify(restTemplate).exchange(eq(""), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));
	}

	@Test
	void updateItem() {
		Long ownerId = 1L;
		long itemId = 100L;
		ItemDto itemDto = new ItemDto();
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

		when(restTemplate.exchange(eq("/" + itemId), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
				.thenReturn(expectedResponse);

		ResponseEntity<Object> result = itemClient.updateItem(ownerId, itemId, itemDto);

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	void getItem() {
		Long userId = 1L;
		Long itemId = 100L;
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

		when(restTemplate.exchange(eq("/" + itemId), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
				.thenReturn(expectedResponse);

		ResponseEntity<Object> result = itemClient.getItem(userId, itemId);

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	void getMyItems() {
		Long ownerId = 1L;
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

		when(restTemplate.exchange(eq(""), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
				.thenReturn(expectedResponse);

		ResponseEntity<Object> result = itemClient.getMyItems(ownerId);

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	void searchItems() {
		String text = "test";
		long userId = 1L;
		Map<String, Object> parameters = Map.of("text", text);
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

		when(restTemplate.exchange(eq("/search"), eq(HttpMethod.GET), any(HttpEntity.class),
				eq(Object.class), eq(parameters)))
				.thenReturn(expectedResponse);

		ResponseEntity<Object> result = itemClient.searchItems(text, userId);

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	void createComment() {
		long userId = 1L;
		long itemId = 100L;
		CreateCommentRequest request = new CreateCommentRequest();
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

		when(restTemplate.exchange(eq("/" + itemId + "/comment"), eq(HttpMethod.POST),
				any(HttpEntity.class), eq(Object.class)))
				.thenReturn(expectedResponse);

		ResponseEntity<Object> result = itemClient.createComment(userId, itemId, request);

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}
}