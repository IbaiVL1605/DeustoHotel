package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.dto.RoomBookingResponse;
import com.example.deusto_hotel.service.RoomBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/room-bookings")
@RequiredArgsConstructor
public class RoomBookingController {

    private final RoomBookingService roomBookingService;

    // Crear
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid List<RoomBookingRequest> request) {
        request.forEach(RoomBookingRequest::validate);

        roomBookingService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
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

    // Validar reserva (Recepcionista)
    @PostMapping("/validar")
    public ResponseEntity<String> validarReserva(@RequestParam String email) {

        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El correo del cliente es obligatorio");
        }

        roomBookingService.validarReserva(email);

        return ResponseEntity.ok("Reserva validada correctamente");
    }

    /*
    // Buscar por habitación
    @GetMapping("/habitacion/{habitacionId}")
    public ResponseEntity<List<RoomBookingResponse>> getByHabitacionId(@PathVariable Long habitacionId) {
        return ResponseEntity.ok(roomBookingService.findByHabitacionId(habitacionId));
    }
    */
}
