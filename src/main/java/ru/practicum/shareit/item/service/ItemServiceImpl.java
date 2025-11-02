package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.ItemDal;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.mapper.ItemMapper;
import ru.practicum.shareit.user.dal.UserDal;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class ItemServiceImpl implements ItemService {
	final UserDal userDb;
	final ItemDal itemDb;
	Long currentMaxId = 0L;

	@Override
	public ItemDto createItem(Long ownerId, ItemDto itemDto) {
		log.info("Service: Проверка существования пользователя. ownerId: {}", ownerId);
		Optional<User> optionalUser = userDb.getUser(ownerId);
		if (optionalUser.isEmpty()) throw new NotFoundException(String.format("Пользователь с id: %d не найден", ownerId));

		Item newItem = ItemMapper.toItem(itemDto);
		newItem.setOwner(ownerId);
		newItem.setId(currentMaxId++);

		log.info("Service: Валидация предмета. currentItem: {} \n", newItem);
		ItemValidator.validateItemToCreate(newItem);

		return ItemMapper.toItemDto(itemDb.putItem(newItem));
	}

	@Override
	public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
		log.info("Service: Проверка существования пользователя. ownerId: {}", ownerId);
		Optional<User> optionalUser = userDb.getUser(ownerId);
		if (optionalUser.isEmpty()) throw new NotFoundException(String.format("Пользователь с id: %d не найден", ownerId));

		Item newItemData = ItemMapper.toItem(itemDto);
		newItemData.setId(itemId);
		newItemData.setOwner(ownerId);

		log.info("Service: Валидация предмета. currentItem: {}", newItemData);
		ItemValidator.validateItemToUpdate(newItemData, itemDb);

		Optional<Item> optionalItem = itemDb.getItem(itemId);
		if (optionalItem.isEmpty()) throw new NotFoundException(String.format("Предмет с id: %d не найден", itemId));

		Item dbItem = optionalItem.get();
		log.info("Service: Старый предмет: {}", dbItem);

		if (newItemData.getName() != null) dbItem.setName(newItemData.getName());
		if (newItemData.getDescription() != null) dbItem.setDescription(newItemData.getDescription());
		if (newItemData.getAvailable() != null) dbItem.setAvailable(newItemData.getAvailable());
		log.info("Service: Новый предмет: {} \n", dbItem);

		return ItemMapper.toItemDto(itemDb.putItem(dbItem));
	}

	@Override
	public ItemDto getItem(Long itemId) {
		log.info("Service: Получение предмета. itemId: {}", itemId);
		Optional<Item> optionalItem = itemDb.getItem(itemId);
		if (optionalItem.isEmpty()) throw new NotFoundException(String.format("Предмет с id: %d не найден", itemId));

		return ItemMapper.toItemDto(optionalItem.get());
	}

	@Override
	public List<ItemDto> getOwnerItems(Long ownerId) {
		log.info("Service: Проверка существования пользователя. ownerId: {}", ownerId);
		Optional<User> optionalUser = userDb.getUser(ownerId);
		if (optionalUser.isEmpty()) throw new NotFoundException(String.format("Пользователь с id: %d не найден", ownerId));

		return itemDb.getOwnersItems(ownerId).stream()
				.map(ItemMapper::toItemDto)
				.toList();
	}

	@Override
	public List<ItemDto> getSearch(String query) {
		return itemDb.getItemByNameOrDescription(query).stream()
				.map(ItemMapper::toItemDto)
				.toList();
	}
}
