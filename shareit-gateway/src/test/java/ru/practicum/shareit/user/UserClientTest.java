package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserClientTest {

	private RestTemplate restTemplate;
	private RestTemplateBuilder restTemplateBuilder;
	private UserClient userClient;

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

		userClient = new UserClient("http://localhost:9999", restTemplateBuilder);
	}

	@Test
	void createUser() {
		UserDto userDto = new UserDto();
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

		when(restTemplate.exchange(eq(""), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
				.thenReturn(expectedResponse);

		ResponseEntity<Object> result = userClient.createUser(userDto);

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	void getUser() {
		long id = 1L;
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

		when(restTemplate.exchange(eq("/" + id), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
				.thenReturn(expectedResponse);

		ResponseEntity<Object> result = userClient.getUser(id);

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	void updateUser() {
		long id = 1L;
		UserDtoUpdate userDtoUpdate = new UserDtoUpdate();
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

		when(restTemplate.exchange(eq("/" + id), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class)))
				.thenReturn(expectedResponse);

		ResponseEntity<Object> result = userClient.updateUser(id, userDtoUpdate);

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	void deleteUser() {
		long id = 1L;
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

		when(restTemplate.exchange(eq("/" + id), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class)))
				.thenReturn(expectedResponse);

		ResponseEntity<Object> result = userClient.deleteUser(id);

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}
}