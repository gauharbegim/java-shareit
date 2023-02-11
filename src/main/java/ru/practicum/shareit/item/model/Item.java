package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.item.ItemStatus;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private Integer id;
    private String name;
    private String description;
    private ItemStatus status;
    private User owner;
    private ItemRequest request;


    public boolean isAvailable(){
        if (status.equals(ItemStatus.OPEN)) {
            return true;
        }else{
            return false;
        }
    }
}