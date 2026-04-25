package com.example.deusto_hotel.unit.controller;

import com.example.deusto_hotel.controller.UserController;
import com.example.deusto_hotel.dto.UserRequest;
import com.example.deusto_hotel.dto.UserResponse;
import com.example.deusto_hotel.model.Role;
import com.example.deusto_hotel.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@Tag("unit")

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    UserController controller;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void registrarUsuario_Success() throws Exception {

        UserRequest request = new UserRequest(
                "Juan",
                "a@gmail.com",
                "12345678"
        );

        UserResponse response = new UserResponse(
                1L,
                "Juan",
                "a@gmail.com",
                Role.CLIENT,
                false,
                LocalDateTime.now()
        );

        when(userService.create(any(UserRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.email").value("a@gmail.com"))
                .andExpect(jsonPath("$.rol").value("CLIENT"))
                .andExpect(jsonPath("$.bloqueado").value(false))
                .andExpect(jsonPath("$.creadoEn").exists());
    }
}