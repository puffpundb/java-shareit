package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dal.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequestWithoutItemsDto;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RequestServiceImplIntegrationTest {

	@Autowired
	private RequestService requestService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RequestRepository requestRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Test
	void createRequestSuccess() {
		User user = new User();
		user.setName("Requester");
		user.setEmail("requester@test.com");
		user = userRepository.save(user);

		ItemRequestWithoutItemsDto requestDto = new ItemRequestWithoutItemsDto();
		requestDto.setDescription("Нужен шуруповёрт");

		ItemRequestWithoutItemsDto result = requestService.createRequest(user.getId(), requestDto);

		assertThat(result.getId()).isNotNull();
		assertThat(result.getDescription()).isEqualTo("Нужен шуруповёрт");

		ItemRequest fromDb = requestRepository.findById(result.getId()).orElseThrow();
		assertThat(fromDb.getRequestor().getId()).isEqualTo(user.getId());
		assertThat(fromDb.getDescription()).isEqualTo("Нужен шуруповёрт");
	}

	@Test
	void getOwnRequestsReturnsRequestsWithItems() {
		User requester = new User();
		requester.setName("Requester2");
		requester.setEmail("requester2@test.com");
		requester = userRepository.save(requester);

		ItemRequest req1 = new ItemRequest();
		req1.setDescription("Нужен молоток");
		req1.setRequestor(requester);
		req1 = requestRepository.save(req1);

		ItemRequest req2 = new ItemRequest();
		req2.setDescription("Нужна пила");
		req2.setRequestor(requester);
		req2 = requestRepository.save(req2);

		Item item1 = new Item();
		item1.setName("Молоток");
		item1.setDescription("Стальной молоток");
		item1.setAvailable(true);
		item1.setOwner(requester);
		item1.setRequestId(req1.getId());
		item1 = itemRepository.save(item1);

		Item item2 = new Item();
		item2.setName("Пила");
		item2.setDescription("Ручная пила");
		item2.setAvailable(true);
		item2.setOwner(requester);
		item2.setRequestId(req2.getId());
		item2 = itemRepository.save(item2);

		List<ItemRequestWithItemsDto> result = requestService.getOwnRequests(requester.getId());

		assertThat(result).hasSize(2);
		ItemRequestWithItemsDto first = result.get(0);
		ItemRequestWithItemsDto second = result.get(1);

		assertThat(first.getId()).isEqualTo(req1.getId());
		assertThat(first.getItems()).hasSize(1);
		assertThat(first.getItems().get(0).getId()).isEqualTo(item1.getId());

		assertThat(second.getId()).isEqualTo(req2.getId());
		assertThat(second.getItems()).hasSize(1);
		assertThat(second.getItems().get(0).getId()).isEqualTo(item2.getId());
	}

	@Test
	void getRequestByIdThrowsWhenNotFound() {
		Long notExistingId = 999L;

		assertThatThrownBy(() -> requestService.getRequestById(notExistingId))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Запрос с id=" + notExistingId + " не найден");
	}
}
