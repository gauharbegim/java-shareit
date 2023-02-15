package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

@Data
public class ItemRequest {
    private Integer id;
    private String description;
    private User requestor;
    private Date created;

}
