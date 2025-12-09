package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingClientTest {

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private RestTemplateBuilder restTemplateBuilder;

	private BookingClient bookingClient;

	@BeforeEach
	void setUp() {
		RestTemplateBuilder chainedBuilder = mock(RestTemplateBuilder.class);
		when(restTemplateBuilder.uriTemplateHandler(any(DefaultUriBuilderFactory.class)))
				.thenReturn(chainedBuilder);
		when(chainedBuilder.requestFactory(any(Supplier.class)))
				.thenReturn(chainedBuilder);
		when(chainedBuilder.build()).thenReturn(restTemplate);

		bookingClient = new BookingClient("http://localhost:9999", restTemplateBuilder);
	}

	@Test
	void getUserBookings() {
		long userId = 1L;
		BookingState state = BookingState.ALL;
		Map<String, Object> parameters = Map.of("state", "ALL");
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

		when(restTemplate.exchange(eq("?state={state}"), eq(HttpMethod.GET), any(HttpEntity.class),
				eq(Object.class), eq(parameters)))
				.thenReturn(expectedResponse);

		ResponseEntity<Object> result = bookingClient.getUserBookings(userId, state);

		assertEquals(HttpStatus.OK, result.getStatusCode());
		verify(restTemplate).exchange(eq("?state={state}"), eq(HttpMethod.GET), any(HttpEntity.class),
				eq(Object.class), eq(parameters));
	}

	@Test
	void getBooking() {
		long userId = 1L;
		Long bookingId = 100L;
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

		when(restTemplate.exchange(eq("/100"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class)))
				.thenReturn(expectedResponse);

		ResponseEntity<Object> result = bookingClient.getBooking(userId, bookingId);

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	void createBooking() {
		long bookerId = 1L;
		BookingDtoRequest request = new BookingDtoRequest();
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

		when(restTemplate.exchange(eq(""), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class)))
				.thenReturn(expectedResponse);

		ResponseEntity<Object> result = bookingClient.createBooking(bookerId, request);

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	void approveBooking() {
		long ownerId = 1L;
		long bookingId = 100L;
		Boolean approve = true;
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

		when(restTemplate.exchange(eq("/100?approved=true"), eq(HttpMethod.PATCH), any(HttpEntity.class),
				eq(Object.class)))
				.thenReturn(expectedResponse);

		ResponseEntity<Object> result = bookingClient.approveBooking(ownerId, bookingId, approve);

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}

	@Test
	void getOwnerBookings() {
		long ownerId = 1L;
		BookingState state = BookingState.CURRENT;
		Map<String, Object> parameters = Map.of("state", "CURRENT");
		ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

		when(restTemplate.exchange(eq("/owner"), eq(HttpMethod.GET), any(HttpEntity.class),
				eq(Object.class), eq(parameters)))
				.thenReturn(expectedResponse);

		ResponseEntity<Object> result = bookingClient.getOwnerBookings(ownerId, state);

		assertEquals(HttpStatus.OK, result.getStatusCode());
	}
}