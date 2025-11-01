package ru.practicum.shareit.request.dal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.HashMap;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestDao {
	final HashMap<Long, ItemRequest> requestDb;
}
