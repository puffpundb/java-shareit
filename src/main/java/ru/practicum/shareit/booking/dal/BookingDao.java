package ru.practicum.shareit.booking.dal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Booking;

import java.util.HashMap;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDao {
	final HashMap<Long, Booking> bookingDb;
}
