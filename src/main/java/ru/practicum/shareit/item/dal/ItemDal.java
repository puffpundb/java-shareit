package ru.practicum.shareit.item.dal;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Slf4j
@Repository
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDal {
	final HashMap<Long, List<Long>> ownersItem = new HashMap<>();
	final HashMap<Long, Item> itemHashMap = new HashMap<>();

	public Optional<Item> getItem(Long id) {
		return Optional.ofNullable(itemHashMap.get(id));
	}

	public Item putItem(Item item) {
		if (ownersItem.containsKey(item.getOwner())) {
			List<Long> ownerItemIds = ownersItem.get(item.getOwner());
			ownerItemIds.add(item.getId());
		} else {
			List<Long> newOwnersItemIdsList = new ArrayList<>();
			newOwnersItemIdsList.add(item.getId());
			ownersItem.put(item.getOwner(), newOwnersItemIdsList);
		}

		itemHashMap.put(item.getId(), item);

		return item;
	}

	public List<Item> getOwnersItems(Long id) { // При работе с БД этот метод удалится, буду использовать getItem для получения предметов владельца
		List<Long> ownersItemsId = ownersItem.getOrDefault(id, List.of());

		return ownersItemsId.stream()
				.map(itemHashMap::get)
				.filter(Objects::nonNull)
				.toList();
	}

	public List<Item> getItemByNameOrDescription(String query) {
		if (query.isBlank()) return new ArrayList<>();

		String lowerCaseQuery = query.toLowerCase();

		return itemHashMap.values().stream()
				.filter(item -> Boolean.TRUE.equals(item.getAvailable()))
				.filter(item -> item.getName().toLowerCase().contains(lowerCaseQuery) ||
						item.getDescription().toLowerCase().contains(lowerCaseQuery))
				.toList();
	}
}
