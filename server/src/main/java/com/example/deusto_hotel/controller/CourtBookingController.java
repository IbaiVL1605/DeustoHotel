package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.CourtBookingRequest;
import com.example.deusto_hotel.dto.CourtBookingResponse;
import com.example.deusto_hotel.service.CourtBookingService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controlador REST encargado de la gestión de reservas de pistas.
 * <p>
 * Expone endpoints para crear, actualizar, eliminar y consultar
 * reservas de pistas deportivas.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/court-bookings")
@RequiredArgsConstructor
public class CourtBookingController {

    /**
     * Servicio encargado de la lógica de negocio
     * de reservas de pistas.
     */
    private final CourtBookingService courtBookingService;

    /*
    // GET ALL
    @GetMapping
    public ResponseEntity<List<CourtBookingResponse>> getAll() {
        return ResponseEntity.ok(courtBookingService.findAll());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<CourtBookingResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(courtBookingService.findById(id));
    }
     */

    /**
     * Crea una nueva reserva de pista.
     *
     * @param request datos de la reserva
     * @param session sesión HTTP del usuario
     * @return reserva creada con estado HTTP 201
     */
    @PostMapping
    public ResponseEntity<CourtBookingResponse> create(
            @RequestBody @Valid CourtBookingRequest request,
            HttpSession session) {

        CourtBookingResponse response =
                courtBookingService.create(request, session);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Actualiza una reserva existente.
     *
     * @param id identificador de la reserva
     * @param request nuevos datos de la reserva
     * @return reserva actualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourtBookingResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid CourtBookingRequest request) {

        CourtBookingResponse response =
                courtBookingService.update(id, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Elimina una reserva de pista.
     *
     * @param id identificador de la reserva
     * @return respuesta sin contenido (HTTP 204)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        courtBookingService.delete(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Obtiene todas las reservas de un cliente.
     *
     * @param clienteId identificador del cliente
     * @return lista de reservas del cliente
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CourtBookingResponse>> getByClienteId(
            @PathVariable Long clienteId) {

        return ResponseEntity.ok(
                courtBookingService.findByClienteId(clienteId)
        );
    }

    /*
    // GET BY PISTA
    @GetMapping("/pista/{pistaId}")
    public ResponseEntity<List<CourtBookingResponse>> getByPistaId(
            @PathVariable Long pistaId) {

        return ResponseEntity.ok(
                courtBookingService.findByPistaId(pistaId)
        );
    }
     */

    @GetMapping
    public ResponseEntity<List<CourtBookingResponse>> getAll() {
        return ResponseEntity.ok(courtBookingService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourtBookingResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(courtBookingService.findById(id));
    }


}