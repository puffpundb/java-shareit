package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemController {
	final ItemClient itemClient;

	@PostMapping
	public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
											 @Valid @RequestBody ItemDto itemDto) {

		return itemClient.createItem(userId, itemDto);
	}

	@PatchMapping("/{itemId}")
	public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long ownerId,
											 @PathVariable long itemId,
											 @RequestBody ItemDto itemDto) {

		return itemClient.updateItem(ownerId, itemId, itemDto);
	}

	@GetMapping("/{itemId}")
	public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") long userId,
										  @PathVariable(name = "itemId") Long itemId) {

		return itemClient.getItem(userId, itemId);
	}

	@GetMapping
	public ResponseEntity<Object> getMyItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {

		return itemClient.getMyItems(ownerId);
	}

	@GetMapping("/search")
	public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") long userId,
											  @RequestParam(name = "search") String text) {
		if (text.isBlank()) return ResponseEntity.ok(List.of());

		return itemClient.searchItems(text, userId);
	}

	@PostMapping("/{itemId}/comment")
	public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long userId,
												@PathVariable long itemId,
												@Valid @RequestBody CreateCommentRequest request) {

		return itemClient.createComment(userId, itemId, request);
	}
}
