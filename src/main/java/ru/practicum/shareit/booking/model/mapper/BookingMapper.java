package ru.practicum.shareit.booking.model.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor
public class BookingMapper {
	public static Booking toNewBooking(BookingDtoRequest bookingDto, Item item, User booker) {
		Booking newBooking = new Booking();
		newBooking.setBooker(booker);
		newBooking.setItem(item);
		newBooking.setStart(bookingDto.getStart());
		newBooking.setEnd(bookingDto.getEnd());
		newBooking.setStatus(Status.WAITING);

		return newBooking;
	}

	public static BookingDto toBookingDto(Booking booking) {
		BookingDto bookingDto = new BookingDto();
		bookingDto.setId(booking.getId());
		bookingDto.setStart(booking.getStart());
		bookingDto.setEnd(booking.getEnd());
		bookingDto.setItem(toItemBookingDto(booking.getItem()));
		bookingDto.setBooker(toBookerDto(booking.getBooker()));
		bookingDto.setStatus(booking.getStatus());

		return bookingDto;
	}

	public static BookingInfo toBookingInfo(Booking booking) {
		BookingInfo info = new BookingInfo();
		info.setId(booking.getId());
		info.setBookerId(info.getBookerId());
		info.setStart(booking.getStart());
		info.setEnd(booking.getEnd());

		return info;
	}

	private static BookerDto toBookerDto(User booker) {
		BookerDto bookerDto = new BookerDto();
		bookerDto.setId(booker.getId());

		return bookerDto;
	}

	private static ItemBookingDto toItemBookingDto(Item item) {
		ItemBookingDto itemBookingDto = new ItemBookingDto();
		itemBookingDto.setId(item.getId());
		itemBookingDto.setName(item.getName());

		return itemBookingDto;
	}
}
