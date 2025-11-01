package ru.practicum.shareit.user.dal;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

@Data
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDal {
	final HashMap<Long, User> usersDb;
	final ArrayList<String> usersEmail;
	Long currentMaxId = 0L;

	public User createUser(User user) {
		user.setId(currentMaxId++);

		usersDb.put(user.getId(), user);
		usersEmail.add(user.getEmail());

		return user;
	}

	public Optional<User> getUser(Long id) {
		return Optional.of(usersDb.get(id));
	}

	public User updateUser(User newUserData) {
		User dbUser = usersDb.get(newUserData.getId());

		if (newUserData.getName() != null) dbUser.setName(newUserData.getName());
		if (newUserData.getEmail() != null) {
			dbUser.setEmail(newUserData.getEmail());
			usersEmail.add(newUserData.getEmail());
		}
		usersDb.put(dbUser.getId(), dbUser);

		return dbUser;
	}

	public User deleteUser(Long id) {
		return usersDb.remove(id);
	}
}
