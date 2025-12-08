package ru.practicum.shareit.exception.handler;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorResponse {
	String error;
	List<String> errors;

	public ErrorResponse(String error) {
		this.error = error;
	}

	public ErrorResponse(List<String> errors) {
		this.errors = errors;
	}
}
