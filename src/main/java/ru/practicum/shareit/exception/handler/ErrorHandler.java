package ru.practicum.shareit.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.ExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleNotFound(final NotFoundException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleValidationException(final ValidationException e) {
		return new ErrorResponse(e.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorResponse handleExistException(final ExistException e) {
		return new ErrorResponse(e.getMessage());
	}
}
