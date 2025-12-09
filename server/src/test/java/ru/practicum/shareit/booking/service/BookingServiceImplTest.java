package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

	@Mock
	private BookingRepository bookingDb;

	@Mock
	private ItemService itemSrv;

	@Mock
	private UserService userSrv;

	@InjectMocks
	private BookingServiceImpl bookingService;

	@Test
	void createBooking() {
		Long bookerId = 1L;
		Long itemId = 100L;
		BookingDtoRequest request = new BookingDtoRequest();
		request.setItemId(itemId);
		request.setStart(LocalDateTime.now().plusHours(1));
		request.setEnd(LocalDateTime.now().plusHours(2));

		User booker = new User();
		booker.setId(bookerId);
		booker.setName("Booker");
		booker.setEmail("booker@test.com");

		Item item = new Item();
		item.setId(itemId);
		item.setName("Item");
		item.setAvailable(true);
		User owner = new User();
		owner.setId(2L);
		owner.setName("Owner");
		item.setOwner(owner);

		when(userSrv.checkAndGetUser(bookerId)).thenReturn(booker);
		when(itemSrv.checkAndGetItem(itemId)).thenReturn(item);
		when(bookingDb.existsOverlappingBooking(itemId, request.getStart(), request.getEnd())).thenReturn(false);

		Booking savedBooking = new Booking();
		savedBooking.setId(200L);
		savedBooking.setBooker(booker);
		savedBooking.setItem(item);
		savedBooking.setStart(request.getStart());
		savedBooking.setEnd(request.getEnd());
		savedBooking.setStatus(Status.WAITING);

		when(bookingDb.save(any(Booking.class))).thenReturn(savedBooking);

		BookingDto result = bookingService.createBooking(bookerId, request);

		assertNotNull(result);
		assertEquals(200L, result.getId());
		assertEquals(Status.WAITING, result.getStatus());
		verify(bookingDb).save(any(Booking.class));
	}

	@Test
	void createBooking_ownItem() {
		Long bookerId = 1L;
		Long itemId = 100L;
		BookingDtoRequest request = new BookingDtoRequest();
		request.setItemId(itemId);
		request.setStart(LocalDateTime.now().plusHours(1));
		request.setEnd(LocalDateTime.now().plusHours(2));

		User booker = new User();
		booker.setId(bookerId);
		Item item = new Item();
		item.setId(itemId);
		item.setAvailable(true);
		User owner = new User();
		owner.setId(bookerId);
		item.setOwner(owner);

		when(userSrv.checkAndGetUser(bookerId)).thenReturn(booker);
		when(itemSrv.checkAndGetItem(itemId)).thenReturn(item);

		ValidationException exception = assertThrows(ValidationException.class,
				() -> bookingService.createBooking(bookerId, request));
		assertEquals("Нельзя бронировать свою вещь", exception.getMessage());
	}

	@Test
	void approveBooking() {
		Long ownerId = 2L;
		Long bookingId = 100L;
		Boolean approved = true;

		User booker = new User();
		booker.setId(1L);
		booker.setName("Booker");
		booker.setEmail("booker@test.com");

		Item item = new Item();
		item.setId(100L);
		item.setName("Item");
		item.setAvailable(true);
		User owner = new User();
		owner.setId(ownerId);
		owner.setName("Owner");
		item.setOwner(owner);

		Booking booking = new Booking();
		booking.setId(bookingId);
		booking.setStatus(Status.WAITING);
		booking.setBooker(booker);
		booking.setItem(item);
		booking.setStart(LocalDateTime.now());
		booking.setEnd(LocalDateTime.now().plusHours(1));

		when(bookingDb.findById(bookingId)).thenReturn(Optional.of(booking));
		when(bookingDb.save(any(Booking.class))).thenReturn(booking);

		BookingDto result = bookingService.approveBooking(ownerId, bookingId, approved);

		assertNotNull(result);
		assertEquals(Status.APPROVED, booking.getStatus());
		verify(bookingDb).save(booking);
	}

	@Test
	void getBooking() {
		Long userId = 1L;
		Long bookingId = 100L;

		User booker = new User();
		booker.setId(userId);
		booker.setName("Booker");
		booker.setEmail("booker@test.com");

		Item item = new Item();
		item.setId(100L);
		item.setName("Item");
		item.setAvailable(true);
		User owner = new User();
		owner.setId(2L);
		owner.setName("Owner");
		item.setOwner(owner);

		Booking booking = new Booking();
		booking.setId(bookingId);
		booking.setBooker(booker);
		booking.setItem(item);
		booking.setStatus(Status.APPROVED);

		when(bookingDb.findById(bookingId)).thenReturn(Optional.of(booking));

		BookingDto result = bookingService.getBooking(userId, bookingId);

		assertNotNull(result);
		assertEquals(bookingId, result.getId());
	}

	@Test
	void getUserBookings() {
		Long userId = 1L;
		String state = "ALL";

		User user = new User();
		user.setId(userId);
		when(userSrv.checkAndGetUser(userId)).thenReturn(user);

		User booker = new User();
		booker.setId(userId);
		booker.setName("Booker");
		Item item = new Item();
		item.setId(100L);
		item.setName("Item");
		Booking booking = new Booking();
		booking.setId(1L);
		booking.setBooker(booker);
		booking.setItem(item);
		booking.setStatus(Status.APPROVED);
		List<Booking> bookings = List.of(booking);

		when(bookingDb.findBookingsByBookerIdAndState(eq(userId), eq(state), any(LocalDateTime.class)))
				.thenReturn(bookings);

		List<BookingDto> result = bookingService.getUserBookings(userId, state);

		assertEquals(1, result.size());
		assertEquals(1L, result.get(0).getId());
	}

	@Test
	void getOwnerBookings() {
		Long ownerId = 2L;
		String state = "CURRENT";

		User owner = new User();
		owner.setId(ownerId);
		when(userSrv.checkAndGetUser(ownerId)).thenReturn(owner);

		User booker = new User();
		booker.setId(1L);
		booker.setName("Booker");
		Item item = new Item();
		item.setId(100L);
		item.setName("Item");
		User itemOwner = new User();
		itemOwner.setId(ownerId);
		item.setOwner(itemOwner);
		Booking booking = new Booking();
		booking.setId(1L);
		booking.setBooker(booker);
		booking.setItem(item);
		booking.setStatus(Status.APPROVED);
		List<Booking> bookings = List.of(booking);

		when(bookingDb.findBookingsByOwnerIdAndState(eq(ownerId), eq(state), any(LocalDateTime.class)))
				.thenReturn(bookings);

		List<BookingDto> result = bookingService.getOwnerBookings(ownerId, state);

		assertEquals(1, result.size());
		assertEquals(1L, result.get(0).getId());
	}

	@Test
	void checkAndGetBooking() {
		Long id = 100L;
		Booking booking = new Booking();
		booking.setId(id);
		when(bookingDb.findById(id)).thenReturn(Optional.of(booking));

		Booking result = bookingService.checkAndGetBooking(id);

		assertEquals(booking, result);
	}

	@Test
	void checkAndGetBooking_notFound() {
		Long id = 999L;
		when(bookingDb.findById(id)).thenReturn(Optional.empty());

		NotFoundException exception = assertThrows(NotFoundException.class,
				() -> bookingService.checkAndGetBooking(id));
		assertTrue(exception.getMessage().contains("Бронирование с id: 999 не найдено"));
	}
}
