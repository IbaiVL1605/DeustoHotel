package com.example.deusto_hotel.unit.controller;

import com.example.deusto_hotel.controller.UserController;
import com.example.deusto_hotel.dto.UserRequest;
import com.example.deusto_hotel.dto.UserResponse;
import com.example.deusto_hotel.exception.Excepciones;
import com.example.deusto_hotel.model.Role;
import com.example.deusto_hotel.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@Tag("unit")

@org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest(UserController.class)
public class UserControllerTest2 {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

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

    @Test
    public void testLogin_CorreoVacio() throws Exception {
        mockMvc.perform(post("/api/v1/users/login")
                        .param("correo", "")
                        .param("contrasena", "12345678"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El correo es obligatorio."));

        verify(userService, never()).login(anyString(), anyString());
    }

    @Test
    public void testLogin_ContrasenaVacia() throws Exception {
        mockMvc.perform(post("/api/v1/users/login")
                        .param("correo", "a@gmail.com")
                        .param("contrasena", ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La contrasena es obligatoria."));

        verify(userService, never()).login(anyString(), anyString());
    }

    @Test
    public void testLogin_UsuarioNoEncontrado() throws Exception {
        when(userService.login("noexiste@gmail.com", "12345678"))
                .thenThrow(new Excepciones.UsuarioNoEncontradoException("Usuario no encontrado"));

        mockMvc.perform(post("/api/v1/users/login")
                        .param("correo", "noexiste@gmail.com")
                        .param("contrasena", "12345678"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).login("noexiste@gmail.com", "12345678");
    }

    @Test
    public void testLogin_CredencialesInvalidas() throws Exception {
        when(userService.login("a@gmail.com", "mal"))
                .thenThrow(new Excepciones.CredencialesInvalidasException("Contrasena incorrecta"));

        mockMvc.perform(post("/api/v1/users/login")
                        .param("correo", "a@gmail.com")
                        .param("contrasena", "mal"))
                .andExpect(status().isUnauthorized());

        verify(userService, times(1)).login("a@gmail.com", "mal");
    }

    @Test
    public void testLogin_UsuarioBloqueado() throws Exception {
        when(userService.login("bloqueado@gmail.com", "12345678"))
                .thenThrow(new Excepciones.UsuarioBloqueadoException("Usuario bloqueado"));

        mockMvc.perform(post("/api/v1/users/login")
                        .param("correo", "bloqueado@gmail.com")
                        .param("contrasena", "12345678"))
                .andExpect(status().isForbidden());

        verify(userService, times(1)).login("bloqueado@gmail.com", "12345678");
    }


}