package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.BookingInfo;

import java.util.List;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
	Long id;

	String name;

	String description;

	Boolean available;

	Long ownerId;

	Long requestId;

	BookingInfo lastBooking;

	BookingInfo nextBooking;

	List<CommentDto> comments;
}
