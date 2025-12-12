package ru.practicum.shareit.exception;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GatewayErrorResponse {
	String error;
	List<String> errors;

	public GatewayErrorResponse(String error) {
		this.error = error;
	}

	public GatewayErrorResponse(List<String> errors) {
		this.errors = errors;
	}
}
