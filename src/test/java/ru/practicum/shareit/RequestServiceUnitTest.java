package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.exception.IncorrectParameterException;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Date;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RequestServiceUnitTest {
    ItemRequestRepository mockItemRequestRepository = Mockito.mock(ItemRequestRepository.class);
    UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
    ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);

    private final ItemRequestService requestService = new ItemRequestServiceImpl(mockItemRequestRepository, mockUserRepository, mockItemRepository);

    @Test
    public void shouldNotCreateRequestWithEmtyDescription() {
        User requestor = new User(1, "sss@email.ru", "Sasha");
        Mockito.when(mockUserRepository.findById(1)).thenReturn(Optional.of(requestor));

        ItemRequestDto itemRequestDto = new ItemRequestDto(null, "коньки для катания", null, new Date(), null);

        ItemRequestDto newItemRequestDto = requestService.addItemRequest(1, itemRequestDto);

        Assertions.assertNotNull(newItemRequestDto);
        Assertions.assertEquals(newItemRequestDto.getDescription(), itemRequestDto.getDescription());
    }


    @Test
    public void shouldReturnIncorrectParamException() {
        User requestor = new User(1, "eee@email.ru", "Eva");
        Mockito.when(mockUserRepository.findById(1)).thenReturn(Optional.of(requestor));
        IncorrectParameterException exception = Assertions.assertThrows(IncorrectParameterException.class,
                () -> requestService.getItemRequest(1, 1)
        );

        Assertions.assertEquals("Запрос не найден", exception.getParameter());
    }
}
