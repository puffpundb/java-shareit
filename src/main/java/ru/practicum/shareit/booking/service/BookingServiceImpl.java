package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.booking.model.mapper.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceImpl implements BookingService{
	final BookingRepository bookingDb;
	final ItemService itemSrv;
	final UserService userSrv;


	@Override
	@Transactional
	public BookingDto createBooking(Long bookerId, BookingDtoRequest bookingDtoRequest) {
		User booker = userSrv.checkAndGetUser(bookerId);
		Item item = itemSrv.checkAndGetItem(bookingDtoRequest.getItemId());

		if (item.getOwner().getId().equals(bookerId)) throw new ValidationException("Нельзя бронировать свою вещь");
		if (!item.getAvailable()) throw new ValidationException("Вещь недоступна для аренды");
		if (bookingDtoRequest.getEnd().isBefore(bookingDtoRequest.getStart()) || bookingDtoRequest.getEnd().equals(bookingDtoRequest.getStart())) {
			throw new ValidationException("Дата окончания должна быть позже даты начала");
		}
		if (bookingDb.existsOverlappingBooking(item.getId(), bookingDtoRequest.getStart(), bookingDtoRequest.getEnd())) {
			throw new ValidationException("Вещь уже забронирована на эти даты");
		}

		Booking newBooking = BookingMapper.toNewBooking(bookingDtoRequest, item, booker);

		return BookingMapper.toBookingDto(bookingDb.save(newBooking));
	}

	@Override
	@Transactional
	public BookingDto approveBooking(Long ownerId, Long bookingId, Boolean approved) {
		Booking booking = checkAndGetBooking(bookingId);

		if (booking.getStatus() != Status.WAITING) {
			throw new ValidationException("Нельзя подтвердить бронирование, не находящееся в статусе WAITING");
		}

		if (!booking.getItem().getOwner().getId().equals(ownerId)) {
			throw new ValidationException("Только владелец вещи может подтверждать бронирование");
		}

		if (approved) booking.setStatus(Status.APPROVED);
		else booking.setStatus(Status.REJECTED);

		return BookingMapper.toBookingDto(bookingDb.save(booking));
	}

	@Override
	@Transactional(readOnly = true)
	public BookingDto getBooking(Long userId, Long bookingId) {
		Booking booking = checkAndGetBooking(bookingId);

		Long bookerId = booking.getBooker().getId();
		Long ownerId = booking.getItem().getOwner().getId();

		if (!bookerId.equals(userId) && !ownerId.equals(userId)) {
			throw new ValidationException("Просмотр бронирования доступен только владельцу вещи или автору бронирования");
		}

		return BookingMapper.toBookingDto(booking);
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookingDto> getUserBookings(Long userId, String state) {
		userSrv.checkAndGetUser(userId);

		List<String> validStates = Arrays.stream(States.values()).map(States::name).toList();

		if (!validStates.contains(state.toUpperCase())) {
			throw new ValidationException("Неизвестный статус: " + state);
		}

		LocalDateTime now = LocalDateTime.now();
		List<Booking> bookings = bookingDb.findBookingsByBookerIdAndState(userId, state, now);

		return bookings.stream()
				.map(BookingMapper::toBookingDto)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<BookingDto> getOwnerBookings(Long ownerId, String state) {
		userSrv.checkAndGetUser(ownerId);

		List<String> validStates = Arrays.stream(States.values()).map(States::name).toList();

		if (!validStates.contains(state.toUpperCase())) {
			throw new ValidationException("Неизвестный статус: " + state);
		}

		LocalDateTime now = LocalDateTime.now();
		List<Booking> bookings = bookingDb.findBookingsByOwnerIdAndState(ownerId, state, now);

		return bookings.stream()
				.map(BookingMapper::toBookingDto)
				.toList();
	}

	@Override
	public Booking checkAndGetBooking(Long id) {
		log.info("Поиск бронирования по id: {}", id);
		return bookingDb.findById(id).orElseThrow(() ->
				new NotFoundException(String.format("Бронирование с id: %d не найдено", id)));
	}
}
