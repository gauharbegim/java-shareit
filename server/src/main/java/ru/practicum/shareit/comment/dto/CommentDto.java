package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class CommentDto {
    private Integer id;
    private String text;
    private Item item;
    private AuthorDto author;
    private String authorName;
    private Date created;
}
