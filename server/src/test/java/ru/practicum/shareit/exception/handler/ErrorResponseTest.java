package ru.practicum.shareit.exception.handler;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ErrorResponseTest {

	@Test
	void constructorWithString_setsError() {
		ErrorResponse response = new ErrorResponse("error");
		assertEquals("error", response.getError());
		assertNull(response.getErrors());
	}

	@Test
	void constructorWithList_setsErrors() {
		List<String> list = List.of("e1", "e2");
		ErrorResponse response = new ErrorResponse(list);
		assertEquals(list, response.getErrors());
		assertNull(response.getError());
	}
}
