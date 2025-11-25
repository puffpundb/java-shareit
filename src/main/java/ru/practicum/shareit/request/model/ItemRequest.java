package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class ItemRequest {
	@Id
	Long id;

	String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "requestor_id", nullable = false)
	User requestor;

	LocalDateTime created;
}
