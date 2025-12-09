package ru.practicum.shareit.request.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequestWithoutItemsDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

import static ru.practicum.shareit.item.controller.ItemController.USER_ID_HEADER;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestController {
	RequestService requestService;

	@PostMapping //ready
	@ResponseStatus(HttpStatus.CREATED)
	public ItemRequestWithoutItemsDto createRequest(@RequestHeader(USER_ID_HEADER) Long userId,
													@RequestBody ItemRequestWithoutItemsDto itemRequestWithoutItemsDto) {
		log.info("RequestController: Запрос на создание реквеста. id: {}, ItemRequest: {}", userId, itemRequestWithoutItemsDto);

		return requestService.createRequest(userId, itemRequestWithoutItemsDto);
	}

	@GetMapping //ready
	public List<ItemRequestWithItemsDto> getOwnRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
		log.info("RequestController: Запрос на получение реквестов. id: {}", userId);

		return requestService.getOwnRequests(userId);
	}

	@GetMapping("/all") //r
	public List<ItemRequestWithoutItemsDto> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
		log.info("RequestController: Запрос на получение всех реквестов. id: {}", userId);

		return requestService.getOtherRequest(userId);
	}

	@GetMapping("/{requestId}")
	public ItemRequestWithItemsDto getRequestById(@PathVariable Long requestId) {
		log.info("RequestController: Запрос на получение реквеста по id. id: {}", requestId);

		return requestService.getRequestById(requestId);
	}
}
