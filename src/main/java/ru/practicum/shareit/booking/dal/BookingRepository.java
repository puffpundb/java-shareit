package ru.practicum.shareit.booking.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	@Query("SELECT COUNT(b) > 0 FROM Booking b " +
			"WHERE b.item.id = :itemId " +
			"AND b.status = ru.practicum.shareit.booking.model.Status.APPROVED " +
			"AND b.end > :start " +
			"AND b.start < :end")
	boolean existsOverlappingBooking(@Param("itemId") Long itemId,
									 @Param("start") LocalDateTime start,
									 @Param("end") LocalDateTime end);


	@Query("SELECT b FROM Booking b " +
			"WHERE b.booker.id = :bookerId " +
			"AND ( " +
			"  :state = 'ALL' OR " +
			"  (:state = 'WAITING' AND b.status = ru.practicum.shareit.booking.model.Status.WAITING) OR " +
			"  (:state = 'REJECTED' AND b.status = ru.practicum.shareit.booking.model.Status.REJECTED) OR " +
			"  (:state = 'FUTURE' AND b.status = ru.practicum.shareit.booking.model.Status.APPROVED AND b.start > :now) OR " +
			"  (:state = 'CURRENT' AND b.status = ru.practicum.shareit.booking.model.Status.APPROVED AND b.start <= :now AND b.end > :now) OR " +
			"  (:state = 'PAST' AND b.status = ru.practicum.shareit.booking.model.Status.APPROVED AND b.end <= :now) " +
			") " +
			"ORDER BY b.id DESC")
	List<Booking> findBookingsByBookerIdAndState(@Param("bookerId") Long bookerId,
												 @Param("state") String state,
												 @Param("now") LocalDateTime now);

	@Query("SELECT b FROM Booking b " +
			"WHERE b.item.owner.id = :ownerId " +
			"AND ( " +
			"  :state = 'ALL' OR " +
			"  (:state = 'WAITING' AND b.status = ru.practicum.shareit.booking.model.Status.WAITING) OR " +
			"  (:state = 'REJECTED' AND b.status = ru.practicum.shareit.booking.model.Status.REJECTED) OR " +
			"  (:state = 'FUTURE' AND b.status = ru.practicum.shareit.booking.model.Status.APPROVED AND b.start > :now) OR " +
			"  (:state = 'CURRENT' AND b.status = ru.practicum.shareit.booking.model.Status.APPROVED AND b.start <= :now AND b.end > :now) OR " +
			"  (:state = 'PAST' AND b.status = ru.practicum.shareit.booking.model.Status.APPROVED AND b.end <= :now) " +
			") " +
			"ORDER BY b.id DESC")
	List<Booking> findBookingsByOwnerIdAndState(@Param("ownerId") Long ownerId,
												@Param("state") String state,
												@Param("now") LocalDateTime now);

	@Query("SELECT b FROM Booking b " +
			"WHERE b.item.id = :itemId " +
			"AND b.status = ru.practicum.shareit.booking.model.Status.APPROVED " +
			"AND b.end <= :now " +
			"ORDER BY b.end DESC")
	List<Booking> findLastBookings(@Param("itemId") Long itemId,
								   @Param("now") LocalDateTime now);

	@Query("SELECT b FROM Booking b " +
			"WHERE b.item.id = :itemId " +
			"AND b.status = ru.practicum.shareit.booking.model.Status.APPROVED " +
			"AND b.start > :now " +
			"ORDER BY b.start ASC")
	List<Booking> findNextBookings(@Param("itemId") Long itemId,
								   @Param("now") LocalDateTime now);

	@Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
			"FROM Booking b " +
			"WHERE b.booker.id = :bookerId " +
			"AND b.item.id = :itemId " +
			"AND b.status = ru.practicum.shareit.booking.model.Status.APPROVED " +
			"AND b.end < :now")
	boolean existsApprovedBookingByBookerIdAndItemIdAndEndBefore(@Param("bookerId") Long bookerId,
																 @Param("itemId") Long itemId,
																 @Param("now") LocalDateTime now);
}
