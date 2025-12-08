package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.CreateCommentRequest;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/items")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class ItemController {
	ItemService itemService;
	public static final String USER_ID_HEADER = "X-Sharer-User-Id";

	@PostMapping //r
	@ResponseStatus(HttpStatus.CREATED)
	public ItemDto createItem(@RequestHeader(USER_ID_HEADER) Long userId, @Valid @RequestBody ItemDto itemDto) {
		log.info("ItemController: Запрос на создание предмета. UserId: {}", userId);

		return itemService.createItem(userId, itemDto);
	}

	@PatchMapping("/{itemId}") //r
	@ResponseStatus(HttpStatus.OK)
	public ItemDto updateItem(@RequestHeader(USER_ID_HEADER) Long ownerId,
							  @PathVariable Long itemId,
							  @RequestBody ItemDto itemDto) {
		log.info("Controller: Запрос на обновление предмета. OwnerId: {}, itemId: {}, itemDto: {}", ownerId, itemId, itemDto);

		return itemService.updateItem(ownerId, itemId, itemDto);
	}

	@GetMapping("/{itemId}") //r
	public ItemDto getItem(@RequestHeader(USER_ID_HEADER) Long userId, @PathVariable Long itemId) {
		log.info("Controller: Запрос на получение предмета. ItemId: {}", itemId);

		return itemService.getItemById(itemId, userId);
	}

	@GetMapping //r
	@ResponseStatus(HttpStatus.OK)
	public List<ItemDto> getMyItems(@RequestHeader(USER_ID_HEADER) Long ownerId) {
		log.info("Controller: Запрос на получение предметов владельца. OwnerId: {}", ownerId);

		return itemService.getOwnerItems(ownerId);
	}

	@GetMapping("/search")
	@ResponseStatus(HttpStatus.OK)
	public List<ItemDto> searchItems(@RequestParam String text) {
		log.info("ItemController: Запрос на поиск предметов. Строка поиска: {}", text);

		return itemService.getSearch(text);
	}

	@PostMapping("/{itemId}/comment")
	public CommentDto createComment(@RequestHeader(USER_ID_HEADER) Long userId,
									@PathVariable Long itemId,
									@Valid @RequestBody CreateCommentRequest request) {

		return itemService.createComment(userId, itemId, request);
	}
}
