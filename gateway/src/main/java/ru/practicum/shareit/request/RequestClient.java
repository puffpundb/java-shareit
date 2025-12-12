package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestWithoutItemsDto;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestClient extends BaseClient {
	static final String API_PREFIX_REQUEST = "/requests";

	public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
		super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX_REQUEST))
				.requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
				.build()
		);
	}

	public ResponseEntity<Object> createRequest(long id, ItemRequestWithoutItemsDto requestDto) {
		return post("", id, requestDto);
	}

	public ResponseEntity<Object> getOwnRequest(long id) {
		return get("", id);
	}

	public ResponseEntity<Object> getAllRequest(long id) {
		return get("/all", id);
	}

	public ResponseEntity<Object> getRequestById(long reqId) {
		return get("/" + reqId);
	}
}
