package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dal.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequestWithoutItemsDto;
import ru.practicum.shareit.request.model.mapper.RequestMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class RequestServiceImpl implements RequestService {
	final UserService userService;

	final RequestRepository requestDb;

	final ItemRepository itemDb;

	@Override
	public ItemRequestWithoutItemsDto createRequest(Long userId, ItemRequestWithoutItemsDto itemRequestWithoutItemsDto) {
		log.info("RequestService: Создание реквеста. itemRequestWithoutItemsDto: {}", itemRequestWithoutItemsDto);

		ItemRequest itemRequest = RequestMapper.toItemRequest(itemRequestWithoutItemsDto);
		itemRequest.setRequestor(userService.checkAndGetUser(userId));

		ItemRequest savedRequest = requestDb.save(itemRequest);
		log.info("RequestService: Созданный реквест. savedRequest: {}", savedRequest);

		return RequestMapper.toRequestWithoutItemsDto(savedRequest);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemRequestWithItemsDto> getOwnRequests(Long userId) {
		userService.checkAndGetUser(userId);

		log.info("RequestService: Получение реквестов по id пользователя. id: {}", userId);
		List<ItemRequest> requests = requestDb.findByRequestorIdOrderById(userId);
		if (requests.isEmpty()) return Collections.emptyList();

		List<Long> requestsIdList = requests.stream().map(ItemRequest::getId).toList();
		log.info("RequestService: Список id найденныйх реквестов: {}", requestsIdList);

		log.info("RequestService: Поиск вещей по id реквестов");
		List<Item> itemList = itemDb.findByRequestIdIn(requestsIdList);
		Map<Long, List<Item>> itemsByReq = itemList.stream().collect(Collectors.groupingBy(Item::getRequestId));
		log.info("RequestService: Найденные предметы: {}", itemList);

		List<ItemRequestWithItemsDto> requestWithItemsDto = requests.stream()
				.map(RequestMapper::toRequestWithItemsDto)
				.map(request -> {
					List<ItemRequestWithItemsDto.ItemDtoForRequest> itemDtoForRequestList = itemsByReq.getOrDefault(request.getId(), Collections.emptyList())
							.stream()
							.map(RequestMapper::toItemDtoForRequest)
							.toList();

					request.setItems(itemDtoForRequestList);

					return request;
				}).toList();
		log.info("RequestService: Список подготовленных реквестов: {}", requestWithItemsDto);

		return requestWithItemsDto;
	}

	@Override
	public List<ItemRequestWithoutItemsDto> getOtherRequest(Long userId) {
		userService.checkAndGetUser(userId);

		log.info("RequestService: Поиск всех реквестов, кроме тех, что созданы пользователем");
		List<ItemRequest> requests = requestDb.findByRequestorIdNotOrderByCreatedDesc(userId);
		log.info("RequestService: найденные реквесты: {}", requests);

		List<ItemRequestWithoutItemsDto> itemRequestWithoutItemsDtos = requests.stream()
				.map(RequestMapper::toRequestWithoutItemsDto)
				.toList();
		log.info("RequestService: переданные реквесты: {}", itemRequestWithoutItemsDtos);

		return itemRequestWithoutItemsDtos;
	}

	@Override
	public ItemRequestWithItemsDto getRequestById(Long requestId) {
		log.info("RequestService: Поиск реквеста по id. id: {}", requestId);
		ItemRequest request = requestDb.findById(requestId).orElseThrow(() ->
				new NotFoundException("Запрос с id=" + requestId + " не найден"));

		List<Item> items = itemDb.findByRequestId(requestId);
		log.info("RequestService: Найденные предметы: {}", items);

		ItemRequestWithItemsDto dto = RequestMapper.toRequestWithItemsDto(request);
		dto.setItems(items.stream()
				.map(RequestMapper::toItemDtoForRequest)
				.toList());
		log.info("RequestService: Найденные реквесты: {}", dto);

		return dto;
	}
}
