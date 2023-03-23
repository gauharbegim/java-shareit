package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegTest {
    private final UserService userService;

    @Test
    public void shouldSuccessAddRequest() {
        UserDto user = new UserDto(null, "Masha", "mmm@mail.ru");
        UserDto newUser = userService.addUser(user);

        UserDto userById = userService.getUser(newUser.getId());

        Assertions.assertNotNull(userById);
        Assertions.assertEquals(userById.getName(), newUser.getName());

        userService.deleteUser(newUser.getId());

        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> userService.getUser(newUser.getId()));
        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    public void shouldSuccessGetRequwstList() {
        UserDto user = new UserDto(null, "Masha", "mmm@mail.ru");
        userService.addUser(user);

        UserDto user2 = new UserDto(null, "Natasha", "nnn@mail.ru");
        userService.addUser(user2);

        List<UserDto> list = userService.getUsersList();
        Assertions.assertEquals(list.size(), 2);
    }

}
