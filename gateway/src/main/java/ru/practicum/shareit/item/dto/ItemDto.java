package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
	Long id;

	@NotBlank(message = "Имя предмета не должно быть пустым")
	String name;

	@NotBlank(message = "Описание не должно быть пустым")
	String description;

	@NotNull(message = "Статус не должен быть пустым")
	Boolean available;

	Long ownerId;

	Long requestId;
}
