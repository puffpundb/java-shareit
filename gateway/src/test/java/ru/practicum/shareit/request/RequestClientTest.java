package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.request.dto.ItemRequestWithoutItemsDto;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestClientTest {

	private RestTemplate restTemplate;
	private RestTemplateBuilder restTemplateBuilder;
	private RequestClient requestClient;

	@BeforeEach
	void setUp() {
		restTemplate = mock(RestTemplate.class);
		restTemplateBuilder = mock(RestTemplateBuilder.class);

		RestTemplateBuilder chainedBuilder = mock(RestTemplateBuilder.class);
		when(restTemplateBuilder.uriTemplateHandler(any(DefaultUriBuilderFactory.class)))
				.thenReturn(chainedBuilder);
		when(chainedBuilder.requestFactory(any(Supplier.class)))
				.thenReturn(chainedBuilder);
		when(chainedBuilder.build()).thenReturn(restTemplate);

		requestClient = new RequestClient("http://localhost:9999", restTemplateBuilder);
	}

	@Test
	void createRequest() {
		long id = 1L;
		ItemRequestWithoutItemsDto requestDto = new ItemRequestWithoutItemsDto();
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

		when(restTemplate.exchange(eq(""), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
				.thenReturn(expectedResponse);

		ResponseEntity<Object> result = requestClient.createRequest(id, requestDto);

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	void getOwnRequest() {
		long id = 1L;
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

		when(restTemplate.exchange(eq(""), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
				.thenReturn(expectedResponse);

		ResponseEntity<Object> result = requestClient.getOwnRequest(id);

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	void getAllRequest() {
		long id = 1L;
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

		when(restTemplate.exchange(eq("/all"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
				.thenReturn(expectedResponse);

		ResponseEntity<Object> result = requestClient.getAllRequest(id);

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	void getRequestById() {
		long reqId = 100L;
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

		when(restTemplate.exchange(eq("/" + reqId), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
				.thenReturn(expectedResponse);

		ResponseEntity<Object> result = requestClient.getRequestById(reqId);

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}
}