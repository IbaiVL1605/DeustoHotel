package com.example.deusto_hotel.unit.service;

import com.example.deusto_hotel.dto.RoomDisponibleResponse;
import com.example.deusto_hotel.dto.RoomRequest;
import com.example.deusto_hotel.dto.RoomResponse;
import com.example.deusto_hotel.mapper.RoomMapper;
import com.example.deusto_hotel.model.Room;
import com.example.deusto_hotel.model.RoomStatus;
import com.example.deusto_hotel.model.RoomType;
import com.example.deusto_hotel.repository.RoomRepository;
import com.example.deusto_hotel.service.RoomService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@Tag("unit")

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomMapper roomMapper;

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

    @Test
    void getDisponibles_retornaListaDeHabitacionesDisponibles() {
        // Fechas de entrada
        LocalDate entrada = LocalDate.of(2025, 5, 1);
        LocalDate salida = LocalDate.of(2025, 5, 3);

        // Mock de Room
        Room room = mock(Room.class);
        List<Room> rooms = List.of(room);

        // Mock del resultado final
        RoomDisponibleResponse response = mock(RoomDisponibleResponse.class);
        List<RoomDisponibleResponse> responseList = List.of(response);

        // Comportamiento del repo
        when(roomRepository.findRoomDisponibles(entrada, salida))
                .thenReturn(rooms);

        // Comportamiento del mapper
        when(roomMapper.toRoomDisponiblesResponse(rooms))
                .thenReturn(responseList);

        // Ejecutar
        List<RoomDisponibleResponse> result =
                roomService.getDisponibles(entrada, salida);

        // Verificaciones
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(roomRepository).findRoomDisponibles(entrada, salida);
        verify(roomMapper).toRoomDisponiblesResponse(rooms);
    }


    @Test
    void getDisponibles_sinResultados() {

        LocalDate entrada = LocalDate.of(2025, 5, 1);
        LocalDate salida = LocalDate.of(2025, 5, 3);

        when(roomRepository.findRoomDisponibles(entrada, salida))
                .thenReturn(List.of());

        when(roomMapper.toRoomDisponiblesResponse(List.of()))
                .thenReturn(List.of());

        List<RoomDisponibleResponse> result =
                roomService.getDisponibles(entrada, salida);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void delete_Success() {
        when(roomRepository.existsById(1L)).thenReturn(true);

        roomService.delete(1L);

        verify(roomRepository).deleteById(1L);
    }

    @Test
    void delete_NotFound() {
        when(roomRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> roomService.delete(1L));

        verify(roomRepository, never()).deleteById(anyLong());
    }




}