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
import static org.junit.jupiter.api.Assertions.assertEquals;
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





}
