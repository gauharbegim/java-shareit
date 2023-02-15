package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

@Data
public class BookingDto {
    private Date dateBegin;
    private Date dateEnd;
    private Item item;
    private User booker;
    private String status;
}
