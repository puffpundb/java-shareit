package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;

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

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

	@MockBean
	private BookingService bookingService;


	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;


	@Test
	void createBooking() throws Exception {
		Long userId = 1L;

		BookingDtoRequest request = new BookingDtoRequest();
		request.setItemId(100L);
		request.setStart(LocalDateTime.of(2030, 1, 1, 10, 0));
		request.setEnd(LocalDateTime.of(2030, 1, 1, 12, 0));

		BookingDto response = new BookingDto();
		response.setId(1L);
		response.setStatus(Status.WAITING);

		when(bookingService.createBooking(eq(userId), any(BookingDtoRequest.class)))
				.thenReturn(response);

		mockMvc.perform(post("/bookings")
						.header(BookingController.USER_ID_HEADER, userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.status", is("WAITING")));
	}


	@Test
	void approveBooking() throws Exception {
		Long ownerId = 2L;
		Long bookingId = 10L;
		Boolean approved = true;

		BookingDto response = new BookingDto();
		response.setId(bookingId);
		response.setStatus(Status.APPROVED);

		when(bookingService.approveBooking(ownerId, bookingId, approved))
				.thenReturn(response);

		mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
						.header(BookingController.USER_ID_HEADER, ownerId)
						.param("approved", approved.toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(10)))
				.andExpect(jsonPath("$.status", is("APPROVED")));
	}


	@Test
	void getBooking() throws Exception {
		Long userId = 1L;
		Long bookingId = 5L;

		BookingDto response = new BookingDto();
		response.setId(bookingId);
		response.setStatus(Status.APPROVED);

		when(bookingService.getBooking(userId, bookingId)).thenReturn(response);

		mockMvc.perform(get("/bookings/{bookingId}", bookingId)
						.header(BookingController.USER_ID_HEADER, userId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(5)))
				.andExpect(jsonPath("$.status", is("APPROVED")));
	}


	@Test
	void getUserBookings() throws Exception {
		Long userId = 1L;
		String state = "ALL";

		BookingDto dto1 = new BookingDto();
		dto1.setId(1L);
		dto1.setStatus(Status.APPROVED);

		BookingDto dto2 = new BookingDto();
		dto2.setId(2L);
		dto2.setStatus(Status.WAITING);

		when(bookingService.getUserBookings(userId, state)).thenReturn(List.of(dto1, dto2));

		mockMvc.perform(get("/bookings")
						.header(BookingController.USER_ID_HEADER, userId)
						.param("state", state))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[1].status", is("WAITING")));
	}

	@Test
	void getOwnerBookings() throws Exception {
		Long ownerId = 2L;
		String state = "CURRENT";

		BookingDto dto = new BookingDto();
		dto.setId(3L);
		dto.setStatus(Status.CANCELED);

		when(bookingService.getOwnerBookings(ownerId, state)).thenReturn(List.of(dto));

		mockMvc.perform(get("/bookings/owner")
						.header(BookingController.USER_ID_HEADER, ownerId)
						.param("state", state))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(3)))
				.andExpect(jsonPath("$[0].status", is("CANCELED")));
	}
}
