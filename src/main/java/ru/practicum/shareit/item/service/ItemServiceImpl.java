package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingInfo;
import ru.practicum.shareit.booking.model.mapper.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.item.model.mapper.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class ItemServiceImpl implements ItemService {
	final UserService userService;

	final BookingRepository bookingDb;

	final ItemRepository itemDb;

	final CommentRepository commentDb;

	@Override
	@Transactional
	public ItemDto createItem(Long ownerId, ItemDto itemDto) {
		User owner = userService.checkAndGetUser(ownerId);

		Item newItem = ItemMapper.toItem(itemDto);
		newItem.setOwner(owner);

		return ItemMapper.toItemDto(itemDb.save(newItem));
	}

	@Override
	@Transactional
	public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
		User owner = userService.checkAndGetUser(ownerId);

		Item newItemData = ItemMapper.toItem(itemDto);
		newItemData.setId(itemId);
		newItemData.setOwner(owner);

		Item dbItem = checkAndGetItem(itemId);

		log.info("ItemService: Старый предмет: {}", dbItem);
		if (newItemData.getName() != null) dbItem.setName(newItemData.getName());
		if (newItemData.getDescription() != null) dbItem.setDescription(newItemData.getDescription());
		if (newItemData.getAvailable() != null) dbItem.setAvailable(newItemData.getAvailable());
		log.info("ItemService: Новый предмет: {} \n", dbItem);

		return ItemMapper.toItemDto(itemDb.save(dbItem));
	}

	@Override
	@Transactional(readOnly = true)
	public ItemDto getItemById(Long itemId, Long userId) {
		Item item = itemDb.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));

		ItemDto dto = ItemMapper.toItemDto(item);

		dto.setComments(commentDb.findByItemIdOrderByIdDesc(itemId).stream()
						.map(ItemMapper::toCommentDto)
						.toList());

		if (userId.equals(item.getOwner().getId())) {
			LocalDateTime now = LocalDateTime.now();
			dto.setLastBooking(getLastBookingInfo(item.getId(), now).orElse(null));
			dto.setNextBooking(getNextBookingInfo(item.getId(), now).orElse(null));
		}

		return dto;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemDto> getOwnerItems(Long ownerId) {
		User user = userService.checkAndGetUser(ownerId);

		List<Item> itemList = itemDb.findByOwnerIdOrderById(ownerId);
		LocalDateTime now = LocalDateTime.now();

		return itemList.stream().map(item -> {
			ItemDto dto = ItemMapper.toItemDto(item);

			dto.setComments(commentDb.findByItemIdOrderByIdDesc(item.getId()).stream()
							.map(ItemMapper::toCommentDto)
							.toList());

			dto.setLastBooking(getLastBookingInfo(item.getId(), now).orElse(null));
			dto.setNextBooking(getNextBookingInfo(item.getId(), now).orElse(null));
			dto.setComments(commentDb.findByItemIdOrderByIdDesc(item.getId()).stream()
					.map(ItemMapper::toCommentDto)
					.toList());

			return dto;
		}).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemDto> getSearch(String query) {
		return itemDb.searchByNameOrDescriptionIgnoreCase(query).stream()
				.map(ItemMapper::toItemDto)
				.toList();
	}

	@Override
	@Transactional
	public CommentDto createComment(Long userId, Long itemId, CreateCommentRequest commentRequest) {
		User author = userService.checkAndGetUser(userId);
		Item item = checkAndGetItem(itemId);

		if (!hasUserBookedItem(userId, itemId)) {
			throw new ValidationException("Только арендовавшие могут оставлять отзывы");
		}

		Comment comment = new Comment();
		comment.setText(commentRequest.getText());
		comment.setItem(item);
		comment.setAuthor(author);

		return ItemMapper.toCommentDto(commentDb.save(comment));
	}

	@Override
	public Item checkAndGetItem(Long id) {
		log.info("ItemService: Получение предмета с id: {}", id);
		return itemDb.findById(id).orElseThrow(() ->
				new NotFoundException(String.format("Предмет с id: %d не найден", id)));
	}

	private Optional<BookingInfo> getLastBookingInfo(Long itemId, LocalDateTime now) {
		List<Booking> bookings = bookingDb.findLastBookings(itemId, now);
		if (bookings.isEmpty()) return Optional.empty();

		return Optional.of(BookingMapper.toBookingInfo(bookings.getFirst()));
	}

	private Optional<BookingInfo> getNextBookingInfo(Long itemId, LocalDateTime now) {
		List<Booking> bookings = bookingDb.findNextBookings(itemId, now);
		if (bookings.isEmpty()) return Optional.empty();

		return Optional.of(BookingMapper.toBookingInfo(bookings.getFirst()));
	}

	private boolean hasUserBookedItem(Long userId, Long itemId) {
		return bookingDb.existsApprovedBookingByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now());
	}
}
