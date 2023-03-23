package ru.practicum.shareit;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class BookServiceIntegTest {
    private final EntityManager entityManager;
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    Date dateBeg = null;
    Date dateEnd = null;
    ItemDto itemDto = null;
    UserDto bookerDto = null;

    @BeforeEach
    public void setup() {
        User owner = new User(1, "eee@email.ru", "Eva");
        UserDto ownerDto = UserMapper.toUserDto(owner);

        User booker = new User(2, "ssss@email.ru", "Sasha");
        bookerDto = UserMapper.toUserDto(booker);

        Item item = new Item(1, "carpet", "description", true, null, owner);
        itemDto = ItemMapper.toItemDto(item);

        dateBeg = getDate("2023-03-29");
        dateEnd = getDate("2023-04-15");

        userService.addUser(ownerDto);
        userService.addUser(bookerDto);

        itemService.addItem(1, itemDto);
    }

    @Test
    public void shouldSuccessBooking() {

        BookingDto bookingDto = new BookingDto(null, dateBeg, dateEnd, 1, itemDto, bookerDto, bookerDto.getId(), null);

        bookingService.booking(bookerDto.getId(), bookingDto);

        TypedQuery<Booking> query = entityManager.createQuery("Select b from Booking b where b.item.id = :item", Booking.class);
        Booking booking = query.setParameter("item", itemDto.getId()).getSingleResult();

        Assertions.assertNotNull(booking.getId());
        Assertions.assertEquals(booking.getBooker().getEmail(), bookerDto.getEmail());
        Assertions.assertEquals(booking.getItem().getName(), itemDto.getName());
    }

    private Date getDate(String stringDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
