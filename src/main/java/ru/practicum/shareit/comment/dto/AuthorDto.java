package ru.practicum.shareit.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthorDto {
    private Integer id;
    private String authorName;
    private String email;
}
