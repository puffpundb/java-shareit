package ru.practicum.shareit.item.model.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;

@NoArgsConstructor
public class ItemMapper {
	public static ItemDto toItemDto(Item item) {
		ItemDto itemDto = new ItemDto();
		itemDto.setId(item.getId());
		itemDto.setName(item.getName());
		itemDto.setDescription(item.getDescription());
		itemDto.setAvailable(item.getAvailable());
		itemDto.setOwnerId(item.getOwner().getId());
//		itemDto.setRequestId(item.getRequest().getId());

		return itemDto;
	}

	public static Item toItem(ItemDto itemDto) {
		Item item = new Item();
		item.setId(itemDto.getId());
		item.setName(itemDto.getName());
		item.setDescription(itemDto.getDescription());
		item.setAvailable(itemDto.getAvailable());

		return item;
	}

	public static CommentDto toCommentDto(Comment comment) {
		CommentDto dto = new CommentDto();
		dto.setId(comment.getId());
		dto.setText(comment.getText());
		dto.setAuthorName(comment.getAuthor().getName());
		dto.setCreated(comment.getCreated());

		return dto;
	}
}
