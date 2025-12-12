package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestWithItemsDto {
	Long id;

	@NotBlank
	String description;

	LocalDateTime created;

	List<ItemDtoForRequest> items;

	@Data
	@NoArgsConstructor
	@FieldDefaults(level = AccessLevel.PRIVATE)
	public static class ItemDtoForRequest {
		Long id;

		String name;

		Long ownerId;
	}
}
