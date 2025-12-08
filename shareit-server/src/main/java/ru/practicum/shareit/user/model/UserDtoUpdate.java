package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDtoUpdate {
	Long id;

	String name;

	@Email(message = "Некорректный email")
	String email;
}
