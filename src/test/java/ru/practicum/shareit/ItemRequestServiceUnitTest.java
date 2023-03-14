package ru.practicum.shareit;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.exception.IncorrectParameterException;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceUnitTest {
    UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
    ItemRepository mockItemRepository = Mockito.mock(ItemRepository.class);
    ItemRequestRepository mockItemRequestRepository = Mockito.mock(ItemRequestRepository.class);

    User requestor = new User(1, "eee@email.ru", "Eva");

    private final ItemRequestServiceImpl itemRequestService = new ItemRequestServiceImpl(mockItemRequestRepository, mockUserRepository, mockItemRepository);

    @Test
    public void shouldReturnIncorrectParamException() {
        Mockito.when(mockUserRepository.findById(1)).thenReturn(Optional.of(requestor));
        IncorrectParameterException exception = Assertions.assertThrows(IncorrectParameterException.class,
                () -> itemRequestService.getItemRequest(1, 1)
        );

        Assertions.assertEquals("Запрос не найден", exception.getParameter());
    }
}
