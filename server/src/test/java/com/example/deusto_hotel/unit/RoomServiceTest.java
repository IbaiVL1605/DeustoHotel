package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.dto.RoomRequest;
import com.example.deusto_hotel.dto.RoomResponse;
import com.example.deusto_hotel.model.Room;
import com.example.deusto_hotel.model.RoomType;
import com.example.deusto_hotel.repository.RoomRepository;
import com.example.deusto_hotel.service.RoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    // Crear habitación
    @Test
    void shouldCreateRoomSuccessfully() {

        RoomRequest request = new RoomRequest("101", RoomType.SUITE, 4, 200.0);

        when(roomRepository.existsByNumero("101")).thenReturn(false);

        Room saved = new Room(1L, RoomType.SUITE);
        /*
        saved.setId(1L);
        saved.setNumero("101");
        saved.setTipo(RoomType.SUITE);
         */
        saved.setNumero("101");
        saved.setCapacidad(4);
        saved.setPrecioPorNoche(200);

        when(roomRepository.save(any(Room.class))).thenReturn(saved);

        RoomResponse result = roomService.create(request);

        assertEquals("101", result.numero());
        assertEquals(RoomType.SUITE, result.tipo());
        assertEquals(4, result.capacidad());

        verify(roomRepository).save(any(Room.class));
    }

    // Error si ya existe el número de habitación
    @Test
    void shouldThrowExceptionIfRoomNumberAlreadyExists() {

        RoomRequest request = new RoomRequest("101", RoomType.SUITE, 4, 200.0);

        when(roomRepository.existsByNumero("101")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                roomService.create(request)
        );

        verify(roomRepository, never()).save(any());
    }

    // Crear habitación INDIVIDUAL
    @Test
    void shouldCreateIndividualRoomWithDefaultValues() {

        RoomRequest request = new RoomRequest("102", RoomType.INDIVIDUAL, 1, 50.0);

        when(roomRepository.existsByNumero("102")).thenReturn(false);

        Room saved = new Room(2L, RoomType.INDIVIDUAL);
        /*
        saved.setId(2L);
        saved.setNumero("102");
        saved.setTipo(RoomType.INDIVIDUAL);
        saved.setCapacidad(RoomType.INDIVIDUAL.getCapacidad());
        saved.setPrecioPorNoche(RoomType.INDIVIDUAL.getPrecioPorNoche());
         */
        saved.setNumero("102");

        when(roomRepository.save(any(Room.class))).thenReturn(saved);

        RoomResponse result = roomService.create(request);

        assertEquals(RoomType.INDIVIDUAL, result.tipo());

        verify(roomRepository).save(any(Room.class));
    }
    //  Crear habitación DOBLE
    @Test
    void shouldCreateDoubleRoomWithDefaultValues() {

        RoomRequest request = new RoomRequest("103", RoomType.DOBLE, 2, 100.0);

        when(roomRepository.existsByNumero("103")).thenReturn(false);

        Room saved = new Room(3L, RoomType.DOBLE);
        /*
        saved.setId(3L);
        saved.setNumero("103");
        saved.setTipo(RoomType.DOBLE);
        saved.setCapacidad(RoomType.DOBLE.getCapacidad());
        saved.setPrecioPorNoche(RoomType.DOBLE.getPrecioPorNoche());
         */

        saved.setNumero("103");

        when(roomRepository.save(any(Room.class))).thenReturn(saved);

        RoomResponse result = roomService.create(request);

        assertEquals(RoomType.DOBLE, result.tipo());

        verify(roomRepository).save(any(Room.class));
    }
}