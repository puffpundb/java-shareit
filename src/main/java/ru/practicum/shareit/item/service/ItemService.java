package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemDtoUpdate;

import java.util.List;

public interface ItemService {
	ItemDto createItem(Long userId, ItemDto item);

	ItemDto updateItem(Long ownerId, Long itemId, ItemDtoUpdate itemDto);

	ItemDto getItem(Long itemId);

	List<ItemDto> getOwnerItems(Long ownerId);

	List<ItemDto> getSearch(String query);
}
