package com.example.deusto_hotel.integration;

import com.example.deusto_hotel.dto.UserRequest;
import com.example.deusto_hotel.dto.UserResponse;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class UserIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldRegisterUserSuccessfully() {

        UserRequest request = new UserRequest("Juan López", "juan@email.com", "juan123");

        ResponseEntity<UserResponse> response = restTemplate.postForEntity(
                "/api/v1/users",
                request,
                UserResponse.class
        );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("juan@email.com", response.getBody().email());
    }

    @Test
    void shouldFailWhenEmailAlreadyExists() {

        UserRequest request = new UserRequest("Juan López", "juan@email.com", "juan123");

        // primer registro OK
        restTemplate.postForEntity("/api/v1/users", request, UserResponse.class);

        // segundo registro (duplicado)
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/users",
                request,
                String.class
        );

        assertEquals(400, response.getStatusCode().value());
    }






    @Test
    void shouldLoginSuccessfully() {
        String email = "login-ok@email.com";
        String password = "juan123";

        registerUserForLogin(email, password);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/users/login?correo={correo}&contrasena={contrasena}",
                null,
                Map.class,
                email,
                password
        );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Sesion iniciada correctamente", response.getBody().get("mensaje"));

        Object usuario = response.getBody().get("usuario");
        assertInstanceOf(Map.class, usuario);
        Map<?, ?> usuarioMap = (Map<?, ?>) usuario;
        assertEquals(email, usuarioMap.get("email"));
    }

    @Test
    void shouldFailLoginWhenPasswordIsInvalid() {
        String email = "login-badpass@email.com";
        registerUserForLogin(email, "password-correcta");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/users/login?correo={correo}&contrasena={contrasena}",
                null,
                String.class,
                email,
                "password-incorrecta"
        );

        assertEquals(401, response.getStatusCode().value());
        assertEquals("Contrasena incorrecta", response.getBody());
    }

    @Test
    void shouldFailLoginWhenUserDoesNotExist() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/users/login?correo={correo}&contrasena={contrasena}",
                null,
                String.class,
                "login-not-found@email.com",
                "12345678"
        );

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Usuario no encontrado", response.getBody());
    }

    private void registerUserForLogin(String email, String password) {
        UserRequest request = new UserRequest("Usuario Login", email, password);
        ResponseEntity<UserResponse> response = restTemplate.postForEntity(
                "/api/v1/users",
                request,
                UserResponse.class
        );
        assertEquals(201, response.getStatusCode().value());
    }

}
