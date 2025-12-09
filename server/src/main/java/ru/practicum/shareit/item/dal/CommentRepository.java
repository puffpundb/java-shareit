package ru.practicum.shareit.item.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByItemIdOrderByIdDesc(Long itemId);

	@Query("SELECT c FROM Comment c WHERE c.itemId IN :itemIds ORDER BY c.id DESC")
	List<Comment> findCommentByItemIds(@Param("itemIds") List<Long> itemIds);
}
