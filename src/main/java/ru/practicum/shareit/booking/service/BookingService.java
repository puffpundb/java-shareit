package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingDtoRequest;

import java.util.List;

public interface BookingService {
	BookingDto createBooking(Long bookerId, BookingDtoRequest bookingDto);

	BookingDto approveBooking(Long ownerId, Long bookingId, Boolean approved);

	BookingDto getBooking(Long userId, Long bookingId);

	List<BookingDto> getUserBookings(Long userId, String state);

	List<BookingDto> getOwnerBookings(Long ownerId, String state);

	Booking checkAndGetBooking(Long id);
}
