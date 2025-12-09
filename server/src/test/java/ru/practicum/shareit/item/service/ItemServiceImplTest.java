package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

	@Mock
	UserService userService;

	@Mock
	BookingRepository bookingDb;

	@Mock
	ItemRepository itemDb;

	@Mock
	CommentRepository commentDb;

	@InjectMocks
	ItemServiceImpl itemService;

	@Test
	void createItem() {
		Long ownerId = 1L;
		User owner = new User();
		owner.setId(ownerId);
		owner.setName("Owner");
		owner.setEmail("owner@test.com");

		ItemDto dto = new ItemDto();
		dto.setName("Item");
		dto.setDescription("Desc");
		dto.setAvailable(true);

		Item saved = new Item();
		saved.setId(10L);
		saved.setName("Item");
		saved.setDescription("Desc");
		saved.setAvailable(true);
		saved.setOwner(owner);

		when(userService.checkAndGetUser(ownerId)).thenReturn(owner);
		when(itemDb.save(any(Item.class))).thenReturn(saved);

		ItemDto result = itemService.createItem(ownerId, dto);

		assertNotNull(result);
		assertEquals(10L, result.getId());
		assertEquals("Item", result.getName());
		verify(itemDb).save(any(Item.class));
	}

	@Test
	void updateItem() {
		Long ownerId = 1L;
		Long itemId = 5L;

		User owner = new User();
		owner.setId(ownerId);
		owner.setName("Owner");

		Item existing = new Item();
		existing.setId(itemId);
		existing.setName("Old");
		existing.setDescription("Old desc");
		existing.setAvailable(false);
		existing.setOwner(owner);

		ItemDto updateDto = new ItemDto();
		updateDto.setName("New");
		updateDto.setDescription("New desc");
		updateDto.setAvailable(true);

		Item saved = new Item();
		saved.setId(itemId);
		saved.setName("New");
		saved.setDescription("New desc");
		saved.setAvailable(true);
		saved.setOwner(owner);

		when(userService.checkAndGetUser(ownerId)).thenReturn(owner);
		when(itemDb.findById(itemId)).thenReturn(Optional.of(existing));
		when(itemDb.save(existing)).thenReturn(saved);

		ItemDto result = itemService.updateItem(ownerId, itemId, updateDto);

		assertNotNull(result);
		assertEquals("New", result.getName());
		assertTrue(result.getAvailable());
		verify(itemDb).save(existing);
	}

	@Test
	void getItemById_owner() {
		Long ownerId = 1L;
		Long itemId = 5L;

		User owner = new User();
		owner.setId(ownerId);
		owner.setName("Owner");

		Item item = new Item();
		item.setId(itemId);
		item.setName("Item");
		item.setDescription("Desc");
		item.setAvailable(true);
		item.setOwner(owner);

		Comment comment = new Comment();
		comment.setId(100L);
		comment.setText("Nice");
		comment.setItem(item);
		comment.setAuthor(owner);
		comment.setCreated(LocalDateTime.now());

		Booking lastBooking = new Booking();
		lastBooking.setId(11L);
		lastBooking.setBooker(owner);
		lastBooking.setItem(item);
		lastBooking.setStart(LocalDateTime.now().minusDays(2));
		lastBooking.setEnd(LocalDateTime.now().minusDays(1));
		lastBooking.setStatus(Status.APPROVED);

		Booking nextBooking = new Booking();
		nextBooking.setId(12L);
		nextBooking.setBooker(owner);
		nextBooking.setItem(item);
		nextBooking.setStart(LocalDateTime.now().plusDays(1));
		nextBooking.setEnd(LocalDateTime.now().plusDays(2));
		nextBooking.setStatus(Status.APPROVED);

		when(itemDb.findById(itemId)).thenReturn(Optional.of(item));
		when(commentDb.findByItemIdOrderByIdDesc(itemId)).thenReturn(List.of(comment));
		when(bookingDb.findLastBookings(eq(itemId), any(LocalDateTime.class))).thenReturn(List.of(lastBooking));
		when(bookingDb.findNextBookings(eq(itemId), any(LocalDateTime.class))).thenReturn(List.of(nextBooking));

		ItemDto result = itemService.getItemById(itemId, ownerId);

		assertNotNull(result);
		assertEquals(itemId, result.getId());
		assertEquals(1, result.getComments().size());
		assertNotNull(result.getLastBooking());
		assertNotNull(result.getNextBooking());
	}

	@Test
	void getItemById_notOwner() {
		Long ownerId = 1L;
		Long userId = 2L;
		Long itemId = 5L;

		User owner = new User();
		owner.setId(ownerId);

		Item item = new Item();
		item.setId(itemId);
		item.setOwner(owner);
		item.setName("Item");
		item.setDescription("Desc");
		item.setAvailable(true);

		when(itemDb.findById(itemId)).thenReturn(Optional.of(item));
		when(commentDb.findByItemIdOrderByIdDesc(itemId)).thenReturn(List.of());

		ItemDto result = itemService.getItemById(itemId, userId);

		assertNotNull(result);
		assertNull(result.getLastBooking());
		assertNull(result.getNextBooking());
	}

	@Test
	void getItemById_notFound() {
		Long itemId = 5L;

		when(itemDb.findById(itemId)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> itemService.getItemById(itemId, 1L));
	}

	@Test
	void getOwnerItems() {
		Long ownerId = 1L;

		User owner = new User();
		owner.setId(ownerId);

		Item item1 = new Item();
		item1.setId(10L);
		item1.setName("Item1");
		item1.setDescription("Desc1");
		item1.setAvailable(true);
		item1.setOwner(owner);

		Item item2 = new Item();
		item2.setId(20L);
		item2.setName("Item2");
		item2.setDescription("Desc2");
		item2.setAvailable(true);
		item2.setOwner(owner);

		Comment comment1 = new Comment();
		comment1.setId(100L);
		comment1.setItemId(10L);
		comment1.setText("c1");
		comment1.setAuthor(owner);
		comment1.setCreated(LocalDateTime.now());

		Booking last1 = new Booking();
		last1.setId(1L);
		last1.setItemId(10L);
		last1.setStatus(Status.APPROVED);

		Booking next1 = new Booking();
		next1.setId(2L);
		next1.setItemId(10L);
		next1.setStatus(Status.APPROVED);

		when(userService.checkAndGetUser(ownerId)).thenReturn(owner);
		when(itemDb.findByOwnerIdOrderById(ownerId)).thenReturn(List.of(item1, item2));
		when(commentDb.findCommentByItemIds(List.of(10L, 20L))).thenReturn(List.of(comment1));
		when(bookingDb.findLastBookingsByItemIds(eq(List.of(10L, 20L)), any(LocalDateTime.class)))
				.thenReturn(List.of(last1));
		when(bookingDb.findNextBookingsByItemIds(eq(List.of(10L, 20L)), any(LocalDateTime.class)))
				.thenReturn(List.of(next1));

		List<ItemDto> result = itemService.getOwnerItems(ownerId);

		assertEquals(2, result.size());
		ItemDto first = result.get(0);
		assertEquals(10L, first.getId());
		assertEquals(1, first.getComments().size());
		assertNotNull(first.getLastBooking());
		assertNotNull(first.getNextBooking());
	}

	@Test
	void getOwnerItems_empty() {
		Long ownerId = 1L;

		User owner = new User();
		owner.setId(ownerId);

		when(userService.checkAndGetUser(ownerId)).thenReturn(owner);
		when(itemDb.findByOwnerIdOrderById(ownerId)).thenReturn(List.of());

		List<ItemDto> result = itemService.getOwnerItems(ownerId);

		assertTrue(result.isEmpty());
	}

	@Test
	void getSearch() {
		String text = "item";

		Item item = new Item();
		item.setId(10L);
		item.setName("Item");
		item.setDescription("Desc");
		item.setAvailable(true);
		User owner = new User();
		owner.setId(1L);
		item.setOwner(owner);

		when(itemDb.searchByNameOrDescriptionIgnoreCase(text)).thenReturn(List.of(item));

		List<ItemDto> result = itemService.getSearch(text);

		assertEquals(1, result.size());
		assertEquals(10L, result.get(0).getId());
	}

	@Test
	void createComment() {
		Long userId = 1L;
		Long itemId = 10L;

		User author = new User();
		author.setId(userId);
		author.setName("User");

		User owner = new User();
		owner.setId(2L);

		Item item = new Item();
		item.setId(itemId);
		item.setOwner(owner);

		CreateCommentRequest request = new CreateCommentRequest();
		request.setText("Nice");

		Comment saved = new Comment();
		saved.setId(100L);
		saved.setText("Nice");
		saved.setItem(item);
		saved.setAuthor(author);
		saved.setCreated(LocalDateTime.now());

		when(userService.checkAndGetUser(userId)).thenReturn(author);
		when(itemDb.findById(itemId)).thenReturn(Optional.of(item));
		when(bookingDb.existsApprovedBookingByBookerIdAndItemIdAndEndBefore(eq(userId), eq(itemId), any(LocalDateTime.class)))
				.thenReturn(true);
		when(commentDb.save(any(Comment.class))).thenReturn(saved);

		CommentDto result = itemService.createComment(userId, itemId, request);

		assertNotNull(result);
		assertEquals(100L, result.getId());
		assertEquals("Nice", result.getText());
	}

	@Test
	void createComment_userNeverBooked() {
		Long userId = 1L;
		Long itemId = 10L;

		User author = new User();
		author.setId(userId);

		Item item = new Item();
		item.setId(itemId);

		CreateCommentRequest request = new CreateCommentRequest();
		request.setText("Nice");

		when(userService.checkAndGetUser(userId)).thenReturn(author);
		when(itemDb.findById(itemId)).thenReturn(Optional.of(item));
		when(bookingDb.existsApprovedBookingByBookerIdAndItemIdAndEndBefore(eq(userId), eq(itemId), any(LocalDateTime.class)))
				.thenReturn(false);

		assertThrows(ValidationException.class, () -> itemService.createComment(userId, itemId, request));
		verify(commentDb, never()).save(any());
	}

	@Test
	void checkAndGetItem() {
		Long itemId = 10L;

		Item item = new Item();
		item.setId(itemId);

		when(itemDb.findById(itemId)).thenReturn(Optional.of(item));

		Item result = itemService.checkAndGetItem(itemId);

		assertEquals(itemId, result.getId());
	}

	@Test
	void checkAndGetItem_notFound() {
		Long itemId = 10L;

		when(itemDb.findById(itemId)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> itemService.checkAndGetItem(itemId));
	}
}
