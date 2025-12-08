package ru.practicum.shareit.request.model.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequestWithoutItemsDto;

@UtilityClass
public class RequestMapper {
	public static ItemRequestWithItemsDto toRequestWithItemsDto(ItemRequest itemRequest) {
		ItemRequestWithItemsDto itemRequestDto = new ItemRequestWithItemsDto();
		itemRequestDto.setId(itemRequest.getId());
		itemRequestDto.setDescription(itemRequest.getDescription());
		itemRequestDto.setCreated(itemRequest.getCreated());

		return itemRequestDto;
	}

	public static ItemRequestWithoutItemsDto toRequestWithoutItemsDto(ItemRequest itemRequest) {
		ItemRequestWithoutItemsDto itemRequestWithoutItemsDto = new ItemRequestWithoutItemsDto();
		itemRequestWithoutItemsDto.setId(itemRequest.getId());
		itemRequestWithoutItemsDto.setDescription(itemRequest.getDescription());
		itemRequestWithoutItemsDto.setCreated(itemRequest.getCreated());

		return itemRequestWithoutItemsDto;
	}

	public static ItemRequest toItemRequest(ItemRequestWithoutItemsDto itemRequestWithoutItemsDto) {
		ItemRequest itemRequest = new ItemRequest();
		itemRequest.setDescription(itemRequestWithoutItemsDto.getDescription());

		return itemRequest;
	}

	public static ItemRequestWithItemsDto.ItemDtoForRequest toItemDtoForRequest(Item item) {
		ItemRequestWithItemsDto.ItemDtoForRequest itemDtoForRequest = new ItemRequestWithItemsDto.ItemDtoForRequest();
		itemDtoForRequest.setId(item.getId());
		itemDtoForRequest.setName(item.getName());
		itemDtoForRequest.setOwnerId(item.getOwner().getId());

		return itemDtoForRequest;
	}
}
