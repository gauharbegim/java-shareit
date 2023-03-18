package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.dao.CommentRepository;
import ru.practicum.shareit.comment.dto.AuthorDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemServiceUnitTest {
    Date dateBeg = getDate("2023-03-29");
    Date dateEnd = getDate("2023-04-15");

    BookingRepository mockBookingRepository = Mockito.mock(BookingRepository.class);
    UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
    ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
    CommentRepository mockCommentRepository = Mockito.mock(CommentRepository.class);

    User owner = new User(1, "eee@email.ru", "Eva");
    Item item = new Item(1, "carpet", "description", true, null, owner);

    User booker = new User(2, "ppp@email.ru", "Polina");
    Booking booking = new Booking(1, dateBeg, dateEnd, item, booker, "APPROVED");

    private final ItemService itemService = new ItemServiceImpl(mockUserRepository, mockBookingRepository, mockItemRepository, mockCommentRepository);

    @Test
    public void shouldNotViewItemBookingsForOtherUser() {
        Mockito.when(mockBookingRepository.findByItem(item)).thenReturn(List.of(booking));
        Mockito.when(mockItemRepository.findById(1)).thenReturn(Optional.of(item));
        User otherUser = new User(3, "sss@email.ru", "Sasha");
        Mockito.when(mockUserRepository.findById(2)).thenReturn(Optional.of(otherUser));

        ItemDto itemDto = itemService.getItem(2, 1);
        Assertions.assertNull(itemDto.getLastBooking());
    }

    @Test
    public void shouldViewItemBookingsForOwner() {
        Mockito.when(mockItemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Mockito.when(mockUserRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        Mockito.when(mockBookingRepository.findByItem(item)).thenReturn(List.of(booking));

        ItemDto itemDto = itemService.getItem(1, 1);
        Assertions.assertNotNull(itemDto.getLastBooking());
    }

    @Test
    public void shouldAddComment() {
        User author = new User(3, "sabrina@email.ru", "Sabrina");
        Mockito.when(mockUserRepository.findById(author.getId())).thenReturn(Optional.of(author));
        Mockito.when(mockItemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        dateBeg = getDate("2023-02-21");
        dateEnd = getDate("2023-03-01");
        Booking authorBooking = new Booking(2, dateBeg, dateEnd, item, author, "APPROVED");
        Mockito.when(mockBookingRepository.findByItemAndBooker(item, author)).thenReturn(List.of(authorBooking));

        AuthorDto authorDto = new AuthorDto(author.getId(), author.getName(), author.getEmail());
        CommentDto comment = new CommentDto(null, "this is test comment", item, authorDto, authorDto.getAuthorName(), new Date());
        CommentDto newComment = itemService.addComment(author.getId(), item.getId(), comment);
        Assertions.assertNotNull(newComment);
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
