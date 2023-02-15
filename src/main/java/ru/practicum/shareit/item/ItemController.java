package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utilits.Variables;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final String pathId = "/{id}";

    @GetMapping()
    public List<ItemDto> getItems(@RequestHeader(value = Variables.USER_ID) Integer ownerId) {
        return itemService.getItems(ownerId);
    }

    @GetMapping(pathId)
    public ItemDto getItem(@PathVariable Integer id) {
        return itemService.getItem(id);
    }

    @GetMapping("/search")
    public List<ItemDto> getItems(@RequestParam(name = "text") String text) {
        return itemService.getItems(text);
    }

    @PostMapping()
    public ItemDto create(@RequestHeader(value = Variables.USER_ID) Integer ownerId,
                          @Valid @RequestBody @NotNull ItemDto item) {
        return itemService.addItem(ownerId, item);
    }

    @PatchMapping(pathId)
    public ItemDto update(@RequestHeader(value = Variables.USER_ID) Integer ownerId,
                          @PathVariable Integer id,
                          @Valid @RequestBody @NotNull ItemDto item) {
        return itemService.update(ownerId, id, item);
    }

}
