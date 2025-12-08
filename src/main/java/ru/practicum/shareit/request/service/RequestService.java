package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequestWithoutItemsDto;

import java.util.List;

public interface RequestService {
	ItemRequestWithoutItemsDto createRequest(Long userId, ItemRequestWithoutItemsDto itemRequestWithoutItemsDto);

	List<ItemRequestWithItemsDto> getOwnRequests(Long userId);

	List<ItemRequestWithoutItemsDto> getOtherRequest(Long userId);

	ItemRequestWithItemsDto getRequestById(Long requestId);
}
