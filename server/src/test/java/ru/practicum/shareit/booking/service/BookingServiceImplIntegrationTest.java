package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceImplIntegrationTest {

	@Autowired
	private BookingService bookingService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private BookingRepository bookingRepository;

	@Test
	void createBookingSuccess() {
		User owner = new User();
		owner.setName("Owner");
		owner.setEmail("owner@test.com");
		owner = userRepository.save(owner);

		User booker = new User();
		booker.setName("Booker");
		booker.setEmail("booker@test.com");
		booker = userRepository.save(booker);

		Item item = new Item();
		item.setName("Item");
		item.setDescription("Desc");
		item.setAvailable(true);
		item.setOwner(owner);
		item = itemRepository.save(item);

		LocalDateTime start = LocalDateTime.now().plusDays(1);
		LocalDateTime end = LocalDateTime.now().plusDays(2);

		BookingDtoRequest request = new BookingDtoRequest();
		request.setItemId(item.getId());
		request.setStart(start);
		request.setEnd(end);

		BookingDto result = bookingService.createBooking(booker.getId(), request);

		assertThat(result.getId()).isNotNull();
		assertThat(result.getItem().getId()).isEqualTo(item.getId());
		assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
		assertThat(result.getStatus()).isEqualTo(Status.WAITING);

		Booking fromDb = bookingRepository.findById(result.getId()).orElseThrow();
		assertThat(fromDb.getItem().getId()).isEqualTo(item.getId());
		assertThat(fromDb.getBooker().getId()).isEqualTo(booker.getId());
		assertThat(fromDb.getStatus()).isEqualTo(Status.WAITING);
	}

	@Test
	void createBookingThrowsWhenBookerIsOwner() {
		User owner = new User();
		owner.setName("Owner");
		owner.setEmail("owner2@test.com");
		owner = userRepository.save(owner);

		Item item = new Item();
		item.setName("Item2");
		item.setDescription("Desc2");
		item.setAvailable(true);
		item.setOwner(owner);
		item = itemRepository.save(item);

		LocalDateTime start = LocalDateTime.now().plusDays(1);
		LocalDateTime end = LocalDateTime.now().plusDays(2);

		BookingDtoRequest request = new BookingDtoRequest();
		request.setItemId(item.getId());
		request.setStart(start);
		request.setEnd(end);

		Long sameUserId = owner.getId();

		assertThatThrownBy(() -> bookingService.createBooking(sameUserId, request))
				.isInstanceOf(ValidationException.class)
				.hasMessage("Нельзя бронировать свою вещь");
	}
}
