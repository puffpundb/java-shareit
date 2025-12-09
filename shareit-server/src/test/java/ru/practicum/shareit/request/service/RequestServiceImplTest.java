package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dal.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequestWithoutItemsDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

	@Mock
	UserService userService;

	@Mock
	RequestRepository requestDb;

	@Mock
	ItemRepository itemDb;

	@InjectMocks
	RequestServiceImpl requestService;

	@Test
	void createRequest() {
		Long userId = 1L;

		User user = new User();
		user.setId(userId);
		user.setName("User");
		user.setEmail("user@test.com");

		ItemRequestWithoutItemsDto createDto = new ItemRequestWithoutItemsDto();
		createDto.setDescription("Need item");

		ItemRequest saved = new ItemRequest();
		saved.setId(10L);
		saved.setDescription("Need item");
		saved.setRequestor(user);
		saved.setCreated(LocalDateTime.now());

		when(userService.checkAndGetUser(userId)).thenReturn(user);
		when(requestDb.save(any(ItemRequest.class))).thenReturn(saved);

		ItemRequestWithoutItemsDto result = requestService.createRequest(userId, createDto);

		assertNotNull(result);
		assertEquals(10L, result.getId());
		assertEquals("Need item", result.getDescription());
		verify(requestDb).save(any(ItemRequest.class));
	}

	@Test
	void getOwnRequests_withItems() {
		Long userId = 1L;

		User user = new User();
		user.setId(userId);

		ItemRequest req1 = new ItemRequest();
		req1.setId(10L);
		req1.setDescription("Req1");
		req1.setRequestor(user);
		req1.setCreated(LocalDateTime.now().minusDays(1));

		ItemRequest req2 = new ItemRequest();
		req2.setId(20L);
		req2.setDescription("Req2");
		req2.setRequestor(user);
		req2.setCreated(LocalDateTime.now());

		Item item1 = new Item();
		item1.setId(100L);
		item1.setName("Item1");
		item1.setRequestId(10L);
		item1.setOwner(user);

		Item item2 = new Item();
		item2.setId(200L);
		item2.setName("Item2");
		item2.setRequestId(10L);
		item2.setOwner(user);

		when(userService.checkAndGetUser(userId)).thenReturn(user);
		when(requestDb.findByRequestorIdOrderById(userId)).thenReturn(List.of(req1, req2));
		when(itemDb.findByRequestIdIn(List.of(10L, 20L))).thenReturn(List.of(item1, item2));

		List<ItemRequestWithItemsDto> result = requestService.getOwnRequests(userId);

		assertEquals(2, result.size());
		ItemRequestWithItemsDto first = result.get(0);
		assertEquals(10L, first.getId());
		assertEquals(2, first.getItems().size());
	}

	@Test
	void getOwnRequests_empty() {
		Long userId = 1L;

		User user = new User();
		user.setId(userId);

		when(userService.checkAndGetUser(userId)).thenReturn(user);
		when(requestDb.findByRequestorIdOrderById(userId)).thenReturn(List.of());

		List<ItemRequestWithItemsDto> result = requestService.getOwnRequests(userId);

		assertTrue(result.isEmpty());
	}

	@Test
	void getOtherRequest() {
		Long userId = 1L;

		User other = new User();
		other.setId(2L);

		ItemRequest req = new ItemRequest();
		req.setId(10L);
		req.setDescription("Other req");
		req.setRequestor(other);
		req.setCreated(LocalDateTime.now());

		when(userService.checkAndGetUser(userId)).thenReturn(new User());
		when(requestDb.findByRequestorIdNotOrderByCreatedDesc(userId)).thenReturn(List.of(req));

		List<ItemRequestWithoutItemsDto> result = requestService.getOtherRequest(userId);

		assertEquals(1, result.size());
		assertEquals(10L, result.get(0).getId());
		assertEquals("Other req", result.get(0).getDescription());
	}

	@Test
	void getRequestById() {
		Long requestId = 10L;

		User user = new User();
		user.setId(1L);

		ItemRequest req = new ItemRequest();
		req.setId(requestId);
		req.setDescription("Req");
		req.setRequestor(user);
		req.setCreated(LocalDateTime.now());

		Item item = new Item();
		item.setId(100L);
		item.setName("Item");
		item.setRequestId(requestId);
		item.setOwner(user);

		when(requestDb.findById(requestId)).thenReturn(Optional.of(req));
		when(itemDb.findByRequestId(requestId)).thenReturn(List.of(item));

		ItemRequestWithItemsDto result = requestService.getRequestById(requestId);

		assertNotNull(result);
		assertEquals(requestId, result.getId());
		assertEquals(1, result.getItems().size());
		assertEquals(100L, result.getItems().get(0).getId());
	}

	@Test
	void getRequestById_notFound() {
		Long requestId = 10L;

		when(requestDb.findById(requestId)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> requestService.getRequestById(requestId));
	}
}
