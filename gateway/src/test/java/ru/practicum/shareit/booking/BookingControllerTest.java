package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingState;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BookingClient bookingClient;

	@Autowired
	private ObjectMapper mapper;

	@Test
	void createBooking() throws Exception {
		String requestJson = "{\"itemId\":1,\"start\":\"2025-12-10T10:00:00\",\"end\":\"2025-12-11T10:00:00\"}";
		String responseJson = "{\"id\":1,\"start\":\"2025-12-10T10:00:00\",\"end\":\"2025-12-11T10:00:00\",\"status\":\"WAITING\"}";

		when(bookingClient.createBooking(eq(1L), any(BookingDtoRequest.class)))
				.thenReturn(ResponseEntity.status(201).body(responseJson.getBytes()));

		mockMvc.perform(post("/bookings")
						.header("X-Sharer-User-Id", 1)
						.contentType("application/json")
						.content(requestJson))
				.andExpect(status().isCreated());
	}

	@Test
	void approveBooking() throws Exception {
		String responseJson = "{\"id\":1,\"status\":\"APPROVED\"}";

		when(bookingClient.approveBooking(eq(1L), eq(1L), eq(true)))
				.thenReturn(ResponseEntity.ok().body(responseJson.getBytes()));

		mockMvc.perform(patch("/bookings/1")
						.header("X-Sharer-User-Id", 1)
						.param("approved", "true"))
				.andExpect(status().isOk());
	}

	@Test
	void getBooking() throws Exception {
		String responseJson = "{\"id\":1,\"status\":\"WAITING\"}";

		when(bookingClient.getBooking(eq(1L), eq(1L)))
				.thenReturn(ResponseEntity.ok().body(responseJson.getBytes()));

		mockMvc.perform(get("/bookings/1")
						.header("X-Sharer-User-Id", 1))
				.andExpect(status().isOk());
	}

	@Test
	void getBookings() throws Exception {
		String responseJson = "[{\"id\":1}]";
		when(bookingClient.getUserBookings(eq(1L), eq(BookingState.ALL)))
				.thenReturn(ResponseEntity.ok().body(responseJson.getBytes()));

		mockMvc.perform(get("/bookings")
						.header("X-Sharer-User-Id", 1)
						.param("state", "ALL"))
				.andExpect(status().isOk());
	}

	@Test
	void getOwnerBookings() throws Exception {
		String responseJson = "[{\"id\":2}]";
		when(bookingClient.getOwnerBookings(eq(1L), eq(BookingState.ALL)))
				.thenReturn(ResponseEntity.ok().body(responseJson.getBytes()));

		mockMvc.perform(get("/bookings/owner")
						.header("X-Sharer-User-Id", 1)
						.param("state", "ALL"))
				.andExpect(status().isOk());
	}
}
