package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBooker(User user);

    Page<Booking> findByBooker(User user, Pageable page);

    List<Booking> findByItem(Item item);

    Page<Booking> findByItem(Item item, Pageable page);

    List<Booking> findByItemAndBooker(Item item, User user);
}
