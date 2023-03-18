package ru.practicum.shareit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class UserControllerWebTest {
    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mockMvc;

    private UserDto userDto;

    @BeforeEach
    private void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        userDto = new UserDto(1, "Sasha", "sss@email.ru");
    }

    @Test
    public void shouldSuccessAddUser() throws Exception {
        Mockito.when(userService.addUser(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())));
    }

    @Test
    public void shouldSuccessGetUsers() throws Exception {
        Mockito.when(userService.getUsersList()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())));
    }

    @Test
    public void shouldSuccessGetUsersById() throws Exception {
        Mockito.when(userService.getUser(any())).thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())));
    }

    @Test
    public void shouldUpdateEmail() throws Exception {
        Mockito.when(userService.updateUser(Mockito.anyInt(), any())).thenReturn(userDto);

        userDto.setEmail("newEmail");
        mockMvc.perform(patch("/users/{id}", 1)
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("newEmail")));
    }
//
//    @Test
//    public void shouldNotGetUserByIdWithoutId() throws Exception {
//        Mockito.doReturn(
//                (new ErrorResponse("Нет пользователя с таким ID"))
//        )
//                .when(Mockito.when(userService.getUser(Mockito.anyInt())).thenThrow(UserNotFoundException.class));
//
//        mockMvc.perform(get("/users/{id}", 1)
//                .characterEncoding(StandardCharsets.UTF_8))
//                .andExpect(status().isNotFound());
//    }

    @Test
    public void shouldDelete() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
