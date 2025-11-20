package ru.practicum.shareit.booking.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

@NoArgsConstructor
public class BookingMapper {
	public BookingDto toBookingDto(Booking booking) {
		BookingDto bookingDto = new BookingDto();
		bookingDto.setId(booking.getId());
		bookingDto.setStart(booking.getStart());
		bookingDto.setEnd(booking.getEnd());
		bookingDto.setThing(booking.getThing());
		bookingDto.setBooker(booking.getBooker());
		bookingDto.setStatus(booking.getStatus());

		return bookingDto;
	}


}
