package ru.practicum.shareit.request.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@NoArgsConstructor
public class RequestMapper {
	public ItemRequestDto toRequestDto(ItemRequest itemRequest) {
		ItemRequestDto itemRequestDto = new ItemRequestDto();
		itemRequestDto.setId(itemRequest.getId());
		itemRequestDto.setDescription(itemRequest.getDescription());
		itemRequestDto.setRequestor(itemRequest.getRequestor());
		itemRequestDto.setCreated(itemRequest.getCreated());

		return itemRequestDto;
	}
}
