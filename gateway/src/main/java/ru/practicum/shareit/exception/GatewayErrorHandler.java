package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GatewayErrorHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GatewayErrorResponse handleValidationException(final MethodArgumentNotValidException e) {
		return new GatewayErrorResponse(
				e.getBindingResult().getFieldErrors().stream()
						.map(error -> error.getField() + ": " + error.getDefaultMessage())
						.toList()
		);
	}

	@ExceptionHandler(MissingRequestHeaderException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GatewayErrorResponse handleMissingHeader(final MissingRequestHeaderException e) {
		return new GatewayErrorResponse("Отсутствует обязательный заголовок: " + e.getHeaderName());
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GatewayErrorResponse handleMissingParam(final MissingServletRequestParameterException e) {
		return new GatewayErrorResponse("Отсутствует обязательный параметр запроса: " + e.getParameterName());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public GatewayErrorResponse handleTypeMismatch(final MethodArgumentTypeMismatchException e) {
		String msg = "Параметр '" + e.getName() + "' имеет некорректное значение: " + e.getValue();
		return new GatewayErrorResponse(msg);
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public GatewayErrorResponse handleOther(final Exception e) {
		return new GatewayErrorResponse("Внутренняя ошибка шлюза: " + e.getClass().getSimpleName());
	}
}
