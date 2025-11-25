package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.CreateCommentRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;

import java.util.List;

public interface ItemService {
	ItemDto createItem(Long userId, ItemDto item);

	ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto);

	ItemDto getItemById(Long itemId, Long userId);

	List<ItemDto> getOwnerItems(Long ownerId);

	List<ItemDto> getSearch(String query);

	Item checkAndGetItem(Long id);

	CommentDto createComment(Long userId, Long itemId, CreateCommentRequest commentRequest);
}
