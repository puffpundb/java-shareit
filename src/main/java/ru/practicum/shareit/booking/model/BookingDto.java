package ru.practicum.shareit.booking.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
	Long id;
	LocalDateTime start;
	LocalDateTime end;
	ItemBookingDto item;
	BookerDto booker;
	Status status;

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	public static class BookerDto {
		Long id;
		String name;
	}

	@Data
	@FieldDefaults(level = AccessLevel.PRIVATE)
	public static class ItemBookingDto {
		Long id;
		String name;
	}
}
