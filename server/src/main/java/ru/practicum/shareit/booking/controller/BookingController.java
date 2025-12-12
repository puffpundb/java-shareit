package ru.practicum.shareit.booking.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingDtoRequest;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/bookings")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class BookingController {
	BookingService bookingService;
	static final String USER_ID_HEADER = "X-Sharer-User-Id";

	@PostMapping //ready
	public BookingDto createBooking(@RequestHeader(USER_ID_HEADER) Long bookerId,
									@RequestBody BookingDtoRequest request) {

		return bookingService.createBooking(bookerId, request);
	}

	@PatchMapping("/{bookingId}") //ready
	public BookingDto approveBooking(@RequestHeader(USER_ID_HEADER) Long ownerId,
									 @PathVariable Long bookingId,
									 @RequestParam Boolean approved) {

		return bookingService.approveBooking(ownerId, bookingId, approved);
	}

	@GetMapping("/{bookingId}") //ready
	public BookingDto getBooking(@RequestHeader(USER_ID_HEADER) Long userId,
								 @PathVariable Long bookingId) {

		return bookingService.getBooking(userId, bookingId);
	}

	@GetMapping //ready
	public List<BookingDto> getUserBookings(@RequestHeader(USER_ID_HEADER) Long userId,
											@RequestParam(defaultValue = "ALL") String state) {

		return bookingService.getUserBookings(userId, state);
	}

	@GetMapping("/owner") //ready
	public List<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
											 @RequestParam(defaultValue = "ALL") String state) {

		return bookingService.getOwnerBookings(ownerId, state);
	}
}
