package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.controller.RoomController;
import com.example.deusto_hotel.dto.RoomRequest;
import com.example.deusto_hotel.dto.RoomResponse;
import com.example.deusto_hotel.model.RoomType;
import com.example.deusto_hotel.service.RoomService;

import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitacionesControllerTest {

    @Mock
    private RoomService roomService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private RoomController controller;


    @Test
    void shouldCreateRoomSuccessfully() {

        RoomRequest request = new RoomRequest(
                "101",
                RoomType.SUITE,
                4,
                200.0
        );

        RoomResponse responseMock = new RoomResponse(
                1L,
                "101",
                RoomType.SUITE,
                4,
                200.0,
                null
        );

        when(roomService.create(request)).thenReturn(responseMock);

        ResponseEntity<RoomResponse> response = controller.create(request, session);

        assertEquals(201, response.getStatusCode().value()); // 🔥 IMPORTANTE
        assertEquals(responseMock, response.getBody());

        verify(roomService).create(request);
    }


    @Test
    void shouldThrowExceptionIfRoomAlreadyExists() {

        RoomRequest request = new RoomRequest(
                "101",
                RoomType.SUITE,
                4,
                200.0
        );

        when(roomService.create(request))
                .thenThrow(new IllegalArgumentException("Ya existe"));

        assertThrows(IllegalArgumentException.class, () ->
                controller.create(request, session)
        );

        verify(roomService).create(request);
    }
}