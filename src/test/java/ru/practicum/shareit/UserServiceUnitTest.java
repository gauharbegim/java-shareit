package ru.practicum.shareit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
    UserRepository mockUserRepository = Mockito.mock(UserRepository.class);

    private final UserServiceImpl userService = new UserServiceImpl(mockUserRepository);

    @Test
    public void shouldExceptionUserNotFound(){
        Mockito.when(mockUserRepository.findById(1)).thenReturn(Optional.empty());
        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.getUser(1));

        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }
}
