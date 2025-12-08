package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingState;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
											  @RequestParam(name = "state", defaultValue = "ALL") String stateParam) {
		BookingState state = BookingState.from(stateParam).orElseThrow(() ->
				new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}", stateParam, userId);

		return bookingClient.getUserBookings(userId, state);
	}

	@GetMapping("/{bookingId}") //ready
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
											 @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);

		return bookingClient.getBooking(userId, bookingId);
	}

	@PostMapping // my
	public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
												@RequestBody @Valid BookingDtoRequest requestDto) {
		return bookingClient.createBooking(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
												 @PathVariable long bookingId,
												 @RequestParam(name = "approved") Boolean approved) {
		return bookingClient.approveBooking(ownerId, bookingId, approved);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") long ownerId,
												   @RequestParam(defaultValue = "ALL") String stateParam) {
		BookingState state = BookingState.from(stateParam).orElseThrow(() ->
				new IllegalArgumentException("Unknown state: " + stateParam));

		return bookingClient.getOwnerBookings(ownerId, state);
	}
}
