package ru.practicum.shareit.item.dal;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Slf4j
@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDal {
	final HashMap<Long, List<Long>> ownersItem = new HashMap<>();
	final HashMap<Long, Item> itemHashMap = new HashMap<>();
	Long currentMaxId = 0L;

	public Optional<Item> getItem(Long id) {
		return Optional.ofNullable(itemHashMap.get(id));
	}

	public Item putItem(Item item) {
		if (item.getId() == null) item.setId(currentMaxId++);

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

	public List<Item> getOwnersItems(Long id) {
		List<Long> ownersItemsId = ownersItem.getOrDefault(id, List.of());

		return ownersItemsId.stream()
				.map(itemHashMap::get)
				.filter(Objects::nonNull)
				.toList();
	}

	public List<Item> getItemByNameOrDescription(String query) {
		String lowerCaseQuery = query.toLowerCase();

		return itemHashMap.values().stream()
				.filter(item -> Boolean.TRUE.equals(item.getAvailable()))
				.filter(item -> item.getName().toLowerCase().contains(lowerCaseQuery) ||
						item.getDescription().toLowerCase().contains(lowerCaseQuery))
				.toList();
	}
}
