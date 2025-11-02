package ru.practicum.shareit.user.dal;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Data
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDal {
	final HashMap<Long, User> usersDb;
	final ArrayList<String> usersEmail;

	public User putUser(User user) {
		usersDb.put(user.getId(), user);
		usersEmail.add(user.getEmail());

		return user;
	}

	public Optional<User> getUser(Long id) {
		return Optional.ofNullable(usersDb.get(id));
	}

	public User deleteUser(Long id) {
		return usersDb.remove(id);
	}
}
