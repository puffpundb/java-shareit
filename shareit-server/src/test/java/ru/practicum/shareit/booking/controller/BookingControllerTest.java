package ru.practicum.shareit.booking.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
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

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

	@Mock
	private BookingService bookingService;

	@InjectMocks
	private BookingController bookingController;

	private MockMvc mockMvc() {
		return MockMvcBuilders.standaloneSetup(bookingController).build();
	}

	@Test
	void createBooking() throws Exception {
		MockMvc mvc = mockMvc();
		Long userId = 1L;

		BookingDtoRequest request = new BookingDtoRequest();
		request.setItemId(100L);
		request.setStart(LocalDateTime.now().plusHours(1));
		request.setEnd(LocalDateTime.now().plusHours(2));

		BookingDto response = new BookingDto();
		response.setId(1L);
		response.setStatus(Status.WAITING);

		when(bookingService.createBooking(eq(userId), any(BookingDtoRequest.class))).thenReturn(response);

		String json = """
                {
                  "itemId": 100,
                  "start": "2030-01-01T10:00:00",
                  "end": "2030-01-01T12:00:00"
                }
                """;

		mvc.perform(post("/bookings")
						.header(BookingController.USER_ID_HEADER, userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.status", is("WAITING")));
	}

	@Test
	void approveBooking() throws Exception {
		MockMvc mvc = mockMvc();
		Long ownerId = 2L;
		Long bookingId = 10L;
		Boolean approved = true;

		BookingDto response = new BookingDto();
		response.setId(bookingId);
		response.setStatus(Status.APPROVED);

		when(bookingService.approveBooking(ownerId, bookingId, approved)).thenReturn(response);

		mvc.perform(patch("/bookings/{bookingId}", bookingId)
						.header(BookingController.USER_ID_HEADER, ownerId)
						.param("approved", "true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(10)))
				.andExpect(jsonPath("$.status", is("APPROVED")));
	}

	@Test
	void getBooking() throws Exception {
		MockMvc mvc = mockMvc();
		Long userId = 1L;
		Long bookingId = 5L;

		BookingDto response = new BookingDto();
		response.setId(bookingId);
		response.setStatus(Status.APPROVED);

		when(bookingService.getBooking(userId, bookingId)).thenReturn(response);

		mvc.perform(get("/bookings/{bookingId}", bookingId)
						.header(BookingController.USER_ID_HEADER, userId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(5)))
				.andExpect(jsonPath("$.status", is("APPROVED")));
	}

	@Test
	void getUserBookings() throws Exception {
		MockMvc mvc = mockMvc();
		Long userId = 1L;
		String state = "ALL";

		BookingDto dto1 = new BookingDto();
		dto1.setId(1L);
		dto1.setStatus(Status.APPROVED);

		BookingDto dto2 = new BookingDto();
		dto2.setId(2L);
		dto2.setStatus(Status.WAITING);

		when(bookingService.getUserBookings(userId, state)).thenReturn(List.of(dto1, dto2));

		mvc.perform(get("/bookings")
						.header(BookingController.USER_ID_HEADER, userId)
						.param("state", state))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[1].status", is("WAITING")));
	}

	@Test
	void getOwnerBookings() throws Exception {
		MockMvc mvc = mockMvc();
		Long ownerId = 2L;
		String state = "CURRENT";

		BookingDto dto = new BookingDto();
		dto.setId(3L);
		dto.setStatus(Status.CANCELED);

		when(bookingService.getOwnerBookings(ownerId, state)).thenReturn(List.of(dto));

		mvc.perform(get("/bookings/owner")
						.header(BookingController.USER_ID_HEADER, ownerId)
						.param("state", state))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(3)))
				.andExpect(jsonPath("$[0].status", is("CANCELED")));
	}
}
