package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {
    private String description;
    private User requestor;
    private Date created;
}
