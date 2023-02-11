package ru.practicum.shareit.item;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final String pathId = "/{id}";

    @GetMapping()
    public Collection<ItemDto> getItems() {
        return null;
    }


    @PostMapping()
    public ItemDto create(@Valid @RequestBody ItemDto item) {
        return null;
    }


    @PutMapping()
    public ResponseEntity<ItemDto> update(@Valid @RequestBody @NotNull ItemDto item) {
        return null;
    }


    @DeleteMapping(pathId)
    public ResponseEntity<ItemDto> deleteItem(@PathVariable int id) {
        return null;
    }

}
