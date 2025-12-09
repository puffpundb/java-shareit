package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoUpdate;

@Service
public class UserClient extends BaseClient {
	private static final String API_PREFIX_USERS = "/users";

	@Autowired
	public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
		super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX_USERS))
				.requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
				.build()
		);
	}

	public ResponseEntity<Object> createUser(UserDto userDto) {
		return post("", userDto);
	}

	public ResponseEntity<Object> getUser(long id) {
		return get("/" + id);
	}

	public ResponseEntity<Object> updateUser(long id, UserDtoUpdate userDtoUpdate) {
		return patch("/" + id, userDtoUpdate);
	}

	public ResponseEntity<Object> deleteUser(long id) {
		return delete("/" + id);
	}
}
