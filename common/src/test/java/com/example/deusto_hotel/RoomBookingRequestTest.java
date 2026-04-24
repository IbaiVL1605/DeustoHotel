package com.example.deusto_hotel;

import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.model.RoomType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class RoomBookingRequestTest {

    // --- GRUPO 1: VALIDACIÓN DE TIPOS INDIVIDUAL/DOBLE ---

    @Test
    void shouldThrowException_WhenIndividualHasRoomId() {
        // GIVEN: Tipo INDIVIDUAL pero con id_habitacion (No permitido)
        RoomBookingRequest request = new RoomBookingRequest(
                RoomType.INDIVIDUAL, 1L, 1, 101L, LocalDate.now(), LocalDate.now().plusDays(1)
        );

        // WHEN & THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, request::validate);
        assertEquals("No se permite especificar habitación para tipos INDIVIDUAL o DOBLE", ex.getMessage());
    }

    @Test
    void shouldThrowException_WhenIndividualMissingQuantity() {
        // GIVEN: Tipo DOBLE pero sin cantidad (Requerido)
        RoomBookingRequest request = new RoomBookingRequest(
                RoomType.DOBLE, 1L, null, null, LocalDate.now(), LocalDate.now().plusDays(1)
        );

        // WHEN & THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, request::validate);
        assertEquals("Se requiere especificar cantidad para tipos INDIVIDUAL o DOBLE", ex.getMessage());
    }

    // --- GRUPO 2: VALIDACIÓN DE TIPO SUITE ---

    @Test
    void shouldThrowException_WhenSuiteMissingRoomId() {
        // GIVEN: Tipo SUITE pero sin id_habitacion (Requerido)
        RoomBookingRequest request = new RoomBookingRequest(
                RoomType.SUITE, 1L, null, null, LocalDate.now(), LocalDate.now().plusDays(1)
        );

        // WHEN & THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, request::validate);
        assertEquals("Se requiere especificar habitación para tipo SUITE", ex.getMessage());
    }

    @Test
    void shouldThrowException_WhenSuiteHasQuantity() {
        // GIVEN: Tipo SUITE pero con cantidad (No permitido, las suites son de 1 en 1 por ID)
        RoomBookingRequest request = new RoomBookingRequest(
                RoomType.SUITE, 1L, 2, 101L, LocalDate.now(), LocalDate.now().plusDays(1)
        );

        // WHEN & THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, request::validate);
        assertEquals("No se permite especificar cantidad para tipo SUITE", ex.getMessage());
    }

    // --- GRUPO 3: CASOS DE ÉXITO (Para cubrir las ramas "false" de los IFs) ---

    @Test
    void shouldNotThrowException_WhenIndividualIsValid() {
        RoomBookingRequest request = new RoomBookingRequest(
                RoomType.INDIVIDUAL, 1L, 2, null, LocalDate.now(), LocalDate.now().plusDays(1)
        );
        assertDoesNotThrow(request::validate);
    }

    @Test
    void shouldNotThrowException_WhenSuiteIsValid() {
        RoomBookingRequest request = new RoomBookingRequest(
                RoomType.SUITE, 1L, null, 202L, LocalDate.now(), LocalDate.now().plusDays(1)
        );
        assertDoesNotThrow(request::validate);
    }

    @Test
    void validate_ShouldContinue_WhenTypeIsNotNull() {
        // Esta prueba cubre la rama "FALSE" del primer IF
        // Al ser un tipo válido, el código pasa el if(tipo == null) y entra al switch
        RoomBookingRequest request = new RoomBookingRequest(
                null, 1L, 1, null, LocalDate.now(), LocalDate.now().plusDays(1)
        );

        assertThrows(IllegalArgumentException.class, request::validate);
    }

}
