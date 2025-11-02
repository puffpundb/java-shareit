package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.ItemDto;

import java.util.List;

public interface ItemService {
	ItemDto createItem(Long userId, ItemDto item);
	ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto);
	ItemDto getItem(Long itemId);
	List<ItemDto> getOwnerItems(Long ownerId);
	List<ItemDto> getSearch(String query);
}
