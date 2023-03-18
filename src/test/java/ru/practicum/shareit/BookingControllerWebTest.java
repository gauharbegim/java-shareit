package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utilits.Variables;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;


@ExtendWith(MockitoExtension.class)
@Slf4j
public class BookingControllerWebTest {
    @Mock
    BookingService bookingService;

    @InjectMocks
    BookingController bookingController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mockMvc;

    private BookingDto bookingDto;

    @BeforeEach
    private void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();
        Date dateBeg = getDate("2023-03-29");
        Date dateEnd = getDate("2023-04-15");

        User owner = new User(1, "eee@email.ru", "Eva");
        Item item = new Item(1, "carpet", "description", true, null, owner);
        ItemDto itemDto = ItemMapper.toItemDto(item);


        bookingDto = new BookingDto(1, dateBeg, dateEnd, 1, itemDto, null, 2, null);
    }

    @Test
    public void shouldNotGetBookingsWithErrorBookingStatus() throws Exception {
        mockMvc.perform(get("/bookings")
                .header(Variables.USER_ID, 2)
                .param("state", "NOSTATE")
                .param("from", "1")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldSuccessAddBooking() throws Exception {
        Mockito.when(bookingService.booking(Mockito.anyInt(), any()))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                .header(Variables.USER_ID, 2)
                .content(mapper.writeValueAsString(bookingDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId())))
                .andExpect(jsonPath("$.bookerId", is(bookingDto.getBookerId())));
    }

//    @Test
//    public void shouldReturnErrorMsg() throws Exception {
//        Mockito.when(bookingService.booking(Mockito.anyInt(), any())).thenThrow(IncorrectParameterException.class);
//
//        mockMvc.perform(post("/bookings")
//                .header("X-Sharer-User-Id", 2)
//                .content(mapper.writeValueAsString(bookingDto))
//                .characterEncoding(StandardCharsets.UTF_8)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }


    @Test
    public void shouldApprove() throws Exception {
        Mockito.when(bookingService.aprove(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", "1")
                .header(Variables.USER_ID, 2)
                .param("approved", "true")
                .content(mapper.writeValueAsString(bookingDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId())))
                .andExpect(jsonPath("$.bookerId", is(bookingDto.getBookerId())));
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
