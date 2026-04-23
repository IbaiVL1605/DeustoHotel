package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.dto.RoomBookingResponse;
import com.example.deusto_hotel.model.RoomType;
import com.example.deusto_hotel.service.RoomBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/room-bookings")
@RequiredArgsConstructor
public class RoomBookingController {

    private final RoomBookingService roomBookingService;

    // Crear
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid List<RoomBookingRequest> request) {
        validarInputCreate(request);

        roomBookingService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private void validarInputCreate(List<RoomBookingRequest> request) {
        for  (RoomBookingRequest roomBookingRequest : request) {
            if(roomBookingRequest.tipo().equals(RoomType.INDIVIDUAL) || roomBookingRequest.tipo().equals(RoomType.DOBLE)) {

                if(roomBookingRequest.id_habitacion() != null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se permite especificar habitación para tipos INDIVIDUAL o DOBLE");
                } if(roomBookingRequest.cantidad() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requiere especificar cantidad para tipos INDIVIDUAL o DOBLE");
                }
            } if(roomBookingRequest.tipo().equals(RoomType.SUITE)) {
                if(roomBookingRequest.id_habitacion() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requiere especificar habitación para tipo SUITE");

                } if(roomBookingRequest.cantidad() != null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se permite especificar cantidad para tipo SUITE");
                }
            }
        }
    }

    /*

    //  Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<RoomBookingResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid RoomBookingRequest request) {

        return ResponseEntity.ok(roomBookingService.update(id, request));
    }
    */

    //  Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {

        roomBookingService.delete(id, userId);

        return ResponseEntity.noContent().build();
    }

    // Buscar por cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<RoomBookingResponse>> getByClienteId(@PathVariable Long clienteId) {
        return ResponseEntity.ok(roomBookingService.findByClienteId(clienteId));
    }

    /*
    // Buscar por habitación
    @GetMapping("/habitacion/{habitacionId}")
    public ResponseEntity<List<RoomBookingResponse>> getByHabitacionId(@PathVariable Long habitacionId) {
        return ResponseEntity.ok(roomBookingService.findByHabitacionId(habitacionId));
    }
    */
}
