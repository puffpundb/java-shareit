package ru.practicum.shareit.item.dal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDao {
	final HashMap<Long, Item> itemDb;
}
