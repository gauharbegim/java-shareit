package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.IncorrectParameterException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
public class BookServiceUnitTest {
    Date dateBeg = getDate("2023-03-29");
    Date dateEnd = getDate("2023-04-15");

    User owner = new User(1, "eee@email.ru", "Eva");

    Item item = new Item(1, "carpet", "description", true, null, owner);

    ItemDto itemDto = ItemMapper.toItemDto(item);

    BookingRepository mockBookingRepository = Mockito.mock(BookingRepository.class);
    UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
    ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
    private final BookingServiceImpl bookingService = new BookingServiceImpl(mockBookingRepository, mockUserRepository, mockItemRepository);

    @Test
    public void shouldReturnExceptionUserNotFound() {
        Mockito.when(mockItemRepository.findById(1)).thenReturn(Optional.of(item));

        BookingDto bookingDto = new BookingDto(1, dateBeg, dateEnd, 1, itemDto, null, 99, null);

        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class,
                () -> bookingService.booking(99, bookingDto));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    public void shouldReturnIncorrectParameterException() {
        Mockito.when(mockItemRepository.findById(1)).thenReturn(Optional.of(item));

        BookingDto bookingDto = new BookingDto(1, dateBeg, dateEnd, 1, itemDto, null, 1, null);

        IncorrectParameterException exception = Assertions.assertThrows(IncorrectParameterException.class,
                () -> bookingService.booking(1, bookingDto));

        Assertions.assertEquals("Неверные параметры", exception.getParameter());
    }

    @Test
    public void shouldReturnBookingNotFoundException() {
        Mockito.when(mockBookingRepository.findById(any())).thenReturn(Optional.empty());

        BookingNotFoundException exception = Assertions.assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBooking(1, 1));

        Assertions.assertEquals("Брони с таким id нет", exception.getMessage());
    }

    @Test
    public void shouldSuccessBook() {
        Mockito.when(mockItemRepository.findById(1)).thenReturn(Optional.of(item));
        User booker = new User(2, "sss@email.ru", "Sasha");
        Mockito.when(mockUserRepository.findById(2)).thenReturn(Optional.of(booker));

        BookingDto bookingDto = new BookingDto(1, dateBeg, dateEnd, 1, itemDto, null, 2, null);

        BookingDto newBooking = bookingService.booking(2, bookingDto);

        Assertions.assertNotNull(newBooking);
    }

    @Test
    public void shouldSuccessGetBooking() {
        Mockito.when(mockItemRepository.findById(1)).thenReturn(Optional.of(item));
        User booker = new User(2, "sss@email.ru", "Sasha");
        Mockito.when(mockUserRepository.findById(2)).thenReturn(Optional.of(booker));

        Booking booking = new Booking(1, dateBeg, dateEnd, item, booker, "APPROVED");
        Mockito.when(mockBookingRepository.findById(1)).thenReturn(Optional.of(booking));

        BookingDto newBooking = bookingService.getBooking(2, 1);

        Assertions.assertNotNull(newBooking);
    }

    @Test
    public void shouldGetBookingWithFailedUser() {
        Mockito.when(mockItemRepository.findById(1)).thenReturn(Optional.of(item));
        User booker = new User(2, "sss@email.ru", "Sasha");
        Mockito.when(mockUserRepository.findById(2)).thenReturn(Optional.of(booker));

        Booking booking = new Booking(1, dateBeg, dateEnd, item, booker, "APPROVED");
        Mockito.when(mockBookingRepository.findById(1)).thenReturn(Optional.of(booking));

        BookingNotFoundException exception = Assertions.assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBooking(3, 1));

        Assertions.assertEquals("Неверные параметры", exception.getMessage());
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
