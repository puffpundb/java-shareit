package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CommentTest {

	@Test
	void setCreatedTimeOnInsert_setsCreated() {
		Comment comment = new Comment();

		assertNull(comment.getCreated());
		comment.setCreatedTimeOnInsert();
		assertNotNull(comment.getCreated());
	}
}
