package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dal.ItemDal;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Slf4j
public class ItemValidator {
	public static void validateItemToCreate(Item item) {
		if (item.getName() == null || item.getName().isBlank()) {
			log.warn("Имя предмета пустое");
			throw new ValidationException("Имя предмета не должно быть пустым");
		}

		if (item.getDescription() == null || item.getDescription().isBlank()) {
			log.warn("Описание предмета пустое");
			throw new ValidationException("Описание не должно быть пустым");
		}

		if (item.getOwner() == null) {
			log.warn("Id владельца пустое предмета пустое");
			throw new ValidationException("Id владельца не должно быть пустым");
		}

		if (item.getAvailable() == null) {
			log.warn("Статус предмета пустой");
			throw new ValidationException("Статус не должен быть пустым");
		}
	}

	public static void validateItemToUpdate(Item item, ItemDal itemDal) {
		if (item.getId() == null) {
			log.warn("Id предмета пустое");
			throw new ValidationException("Id предмета не должен быть пустым");
		}

		if (item.getOwner() == null) {
			log.warn("Id владельца пустое");
			throw new ValidationException("Id владельца не должен быть пустым");
		}

		if (!itemDal.getItemHashMap().containsKey(item.getId())) {
			log.warn("Предмет с id: {} не найден", item.getId());
			throw new NotFoundException(String.format("Предмет с id: %d не найден", item.getId()));
		}

		List<Long> ownersItem = itemDal.getOwnersItem().get(item.getOwner());
		if (!ownersItem.contains(item.getId())) {
			log.warn("Пользователь не является владельцем предмета");
			throw new ValidationException("Пользователь не является владельцем предмета");
		}

		if (item.getName() != null && item.getName().isBlank()) {
			log.warn("Имя предмета пустое");
			throw new ValidationException("Имя предмета не должно быть пустым");
		}

		if (item.getDescription() != null && item.getDescription().isBlank()) {
			log.warn("Описание предмета пустое");
			throw new ValidationException("Описание предмета не должно быть пустым");
		}
	}
}
