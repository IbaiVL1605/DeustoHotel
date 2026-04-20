package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.controller.UserController;
import com.example.deusto_hotel.dto.UserRequest;
import com.example.deusto_hotel.dto.UserResponse;
import com.example.deusto_hotel.model.Role;
import com.example.deusto_hotel.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class UserControllerTest2 {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRegistrarUsuario_Success() throws Exception {
        // Arrange
        UserRequest request = new UserRequest("Juan", "a@gmail.com", "12345678");

        UserResponse response = new UserResponse(
                1L,
                "Juan",
                "a@gmail.com",
                Role.CLIENT,
                false,
                LocalDateTime.now()
        );

        when(userService.create(any(UserRequest.class))).thenReturn(response);

        // Act & Assert
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

        verify(userService, times(1)).create(any(UserRequest.class));
    }

    @Test
    public void testLogin_Success() throws Exception {
        // Arrange
        UserResponse response = new UserResponse(
                1L,
                "Juan",
                "a@gmail.com",
                Role.CLIENT,
                false,
                LocalDateTime.now()
        );

        when(userService.login("a@gmail.com", "12345678")).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users/login")
                        .param("correo", "a@gmail.com")
                        .param("contrasena", "12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Sesion iniciada correctamente"))
                .andExpect(jsonPath("$.usuario.email").value("a@gmail.com"))
                .andExpect(jsonPath("$.usuario.nombre").value("Juan"));

        verify(userService, times(1)).login("a@gmail.com", "12345678");
    }
}