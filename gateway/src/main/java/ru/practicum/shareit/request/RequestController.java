package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestWithoutItemsDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestController {
	final RequestClient requestClient;

	@PostMapping
	public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
												@Valid @RequestBody ItemRequestWithoutItemsDto itemRequestWithoutItemsDto) {

		return requestClient.createRequest(userId, itemRequestWithoutItemsDto);
	}

	@GetMapping
	public ResponseEntity<Object> getOwnRequests(@RequestHeader("X-Sharer-User-Id") long userId) {

		return requestClient.getOwnRequest(userId);
	}

	@GetMapping("/all")
	public ResponseEntity<Object> getAllRequest(@RequestHeader("X-Sharer-User-Id") long userId) {

		return requestClient.getAllRequest(userId);
	}

	@GetMapping("/{requestId}")
	public ResponseEntity<Object> getRequestById(@PathVariable(name = "requestId") long reqId) {

		return requestClient.getRequestById(reqId);
	}
}
