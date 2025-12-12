package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.CreateCommentRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dal.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceImplIntegrationTest {

	@Autowired
	private ItemService itemService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Test
	void createItemSuccess() {
		User owner = new User();
		owner.setName("Owner");
		owner.setEmail("owner@test.com");
		owner = userRepository.save(owner);

		ItemDto request = new ItemDto();
		request.setName("Drill");
		request.setDescription("Power drill");
		request.setAvailable(true);

		ItemDto result = itemService.createItem(owner.getId(), request);

		assertThat(result.getId()).isNotNull();
		assertThat(result.getName()).isEqualTo("Drill");
		assertThat(result.getDescription()).isEqualTo("Power drill");
		assertThat(result.getAvailable()).isTrue();

		Item fromDb = itemRepository.findById(result.getId()).orElseThrow();
		assertThat(fromDb.getOwner().getId()).isEqualTo(owner.getId());
	}

	@Test
	void createCommentSuccessWhenUserHasPastApprovedBooking() {
		User owner = new User();
		owner.setName("Owner");
		owner.setEmail("owner2@test.com");
		owner = userRepository.save(owner);

		User booker = new User();
		booker.setName("Booker");
		booker.setEmail("booker2@test.com");
		booker = userRepository.save(booker);

		Item item = new Item();
		item.setName("Hammer");
		item.setDescription("Steel hammer");
		item.setAvailable(true);
		item.setOwner(owner);
		item = itemRepository.save(item);

		LocalDateTime start = LocalDateTime.now().minusDays(2);
		LocalDateTime end = LocalDateTime.now().minusDays(1);

		Booking booking = new Booking();
		booking.setItem(item);
		booking.setBooker(booker);
		booking.setStart(start);
		booking.setEnd(end);
		booking.setStatus(Status.APPROVED);
		booking = bookingRepository.save(booking);

		CreateCommentRequest request = new CreateCommentRequest();
		request.setText("Отличная вещь");

		CommentDto result = itemService.createComment(booker.getId(), item.getId(), request);

		assertThat(result.getId()).isNotNull();
		assertThat(result.getText()).isEqualTo("Отличная вещь");
		assertThat(result.getAuthorName()).isEqualTo(booker.getName());

		Comment fromDb = commentRepository.findById(result.getId()).orElseThrow();
		assertThat(fromDb.getItem().getId()).isEqualTo(item.getId());
		assertThat(fromDb.getAuthor().getId()).isEqualTo(booker.getId());
	}

	@Test
	void createCommentThrowsWhenUserHasNoBooking() {
		User owner = new User();
		owner.setName("Owner3");
		owner.setEmail("owner3@test.com");
		owner = userRepository.save(owner);

		User user = new User();
		user.setName("UserNoBooking");
		user.setEmail("nobooking@test.com");
		user = userRepository.save(user);

		Item item = new Item();
		item.setName("Saw");
		item.setDescription("Hand saw");
		item.setAvailable(true);
		item.setOwner(owner);
		item = itemRepository.save(item);

		CreateCommentRequest request = new CreateCommentRequest();
		request.setText("Попытка оставить комментарий без бронирования");

		Long userId = user.getId();
		Long itemId = item.getId();

		assertThatThrownBy(() -> itemService.createComment(userId, itemId, request))
				.isInstanceOf(ValidationException.class)
				.hasMessage("Только арендовавшие могут оставлять отзывы");
	}
}
