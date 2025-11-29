package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.ValidationException;

import java.util.Arrays;
import java.util.List;

public enum States {
	ALL,
	CURRENT,
	FUTURE,
	PAST,
	WAITING,
	REJECTED;

	public static void checkState(String state) {
		List<String> validStates = Arrays.stream(values()).map(States::name).toList();
		if (!validStates.contains(state)) throw new ValidationException("Неизвестный статус: " + state);
    }
}
