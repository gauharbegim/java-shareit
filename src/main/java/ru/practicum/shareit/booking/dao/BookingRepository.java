package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query(nativeQuery = true, value = "SELECT b.*, ROWNUM() r FROM booking b WHERE b.USER_ID=?1 ORDER BY DATE_BEGIN ")
    List<Booking> findByBooker(User user);

    @Query(nativeQuery = true, value = "SELECT b.* FROM (SELECT b.*, ROWNUM() r FROM booking b WHERE b.USER_ID=:user ORDER BY DATE_BEGIN) b " +
            "WHERE r>:from and ROWNUM<=:size ORDER BY DATE_BEGIN")
    List<Booking> findByBookerByPage(@Param("user") User user, @Param("from") Integer from, @Param("size") Integer size);

    List<Booking> findByItem(Item item);

    Page<Booking> findByItem(Item item, Pageable page);

    List<Booking> findByItemAndBooker(Item item, User user);
}
