package ru.practicum.shareit.user.service;

import ru.practicum.shareit.exception.ExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dal.UserDal;
import ru.practicum.shareit.user.model.User;

public class UserValidator {
	public static void validateUserToCreate(User currentUserData, UserDal userDal) {
		if (currentUserData.getName() == null || currentUserData.getName().isBlank()) {
			throw new ValidationException("Имя пользователя не должно быть пустым");
		}

		if (currentUserData.getEmail() == null || currentUserData.getEmail().isBlank() || !currentUserData.getEmail().contains("@")) {
			throw new ValidationException("Email не должен быть пустым и должен указывать на сервис электронной почты");
		}

		if (userDal.getUsersEmail().contains(currentUserData.getEmail())) {
			throw new ExistException("Пользователь с данным e-mail уже существует");
		}
	}

	public static void validateUserToUpdate(User currentUserData, UserDal userDal) {
		if (currentUserData.getEmail() != null && (currentUserData.getEmail().isBlank() || !currentUserData.getEmail().contains("@"))) {
			throw new ValidationException("Email не должен быть пустым и должен указывать на сервис электронной почты");
		}

		if (userDal.getUsersEmail().contains(currentUserData.getEmail())) {
			throw new ExistException("Пользователь с данным e-mail уже существует");
		}

		if (!userDal.getUsersDb().containsKey(currentUserData.getId())) {
			throw new NotFoundException(String.format("Пользователь с данным id: %d не существует", currentUserData.getId()));
		}
	}
}
