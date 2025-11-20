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

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class ItemServiceImpl implements ItemService {
	final UserDal userDb;
	final ItemDal itemDb;

	@Override
	public ItemDto createItem(Long ownerId, ItemDto itemDto) {
		checkUser(ownerId);

		Item newItem = ItemMapper.toItem(itemDto);
		newItem.setOwner_id(ownerId);

		return ItemMapper.toItemDto(itemDb.putItem(newItem));
	}

	@Override
	public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
		checkUser(ownerId);

		Item newItemData = ItemMapper.toItem(itemDto);
		newItemData.setId(itemId);
		newItemData.setOwner_id(ownerId);

		Item dbItem = itemDb.getItem(itemId).orElseThrow(() -> new NotFoundException(String.format("Предмет с id: %d не найден", itemId)));

		log.info("ItemService: Старый предмет: {}", dbItem);
		if (newItemData.getName() != null) dbItem.setName(newItemData.getName());
		if (newItemData.getDescription() != null) dbItem.setDescription(newItemData.getDescription());
		if (newItemData.getAvailable() != null) dbItem.setAvailable(newItemData.getAvailable());
		log.info("ItemService: Новый предмет: {} \n", dbItem);

		return ItemMapper.toItemDto(itemDb.putItem(dbItem));
	}

	@Override
	public ItemDto getItem(Long itemId) {
		log.info("ItemService: Получение предмета. itemId: {}", itemId);
		Item dbItem = itemDb.getItem(itemId).orElseThrow(() -> new NotFoundException(String.format("Предмет с id: %d не найден", itemId)));

		return ItemMapper.toItemDto(dbItem);
	}

	@Override
	public List<ItemDto> getOwnerItems(Long ownerId) {
		checkUser(ownerId);

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

	private void checkUser(Long ownerId) {
		log.info("ItemService: Проверка существования пользователя. ownerId: {}", ownerId);
		userDb.getUser(ownerId).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id: %d не найден", ownerId)));
	}
}
