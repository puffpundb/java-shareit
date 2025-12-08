package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
	private static final String API_PREFIX_ITEMS = "/items";

	public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
		super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX_ITEMS))
				.requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
				.build()
		);
	}

	public ResponseEntity<Object> createItem(long userId, @Valid ItemDto itemDto) {
		return post("", userId, itemDto);
	}


	public ResponseEntity<Object> updateItem(Long ownerId, long itemId, ItemDto itemDto) {
		return patch("/" + itemId, ownerId, itemDto);
	}

	public ResponseEntity<Object> getItem(Long userId, Long itemId) {
		return get("/" + itemId, userId);
	}

	public ResponseEntity<Object> getMyItems(Long ownerId) {
		return get("", ownerId);
	}

	public ResponseEntity<Object> searchItems(String text, long userId) {
		Map<String, Object> param = Map.of("text", text);

		return get("/search", userId, param);
	}

	public ResponseEntity<Object> createComment(long userId, long itemId, @Valid CreateCommentRequest request) {
		return post("/" + itemId + "/comment", userId, request);
	}
}
