package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.dto.RoomBookingResponse;
import com.example.deusto_hotel.service.RoomBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/room-bookings")
@RequiredArgsConstructor
public class RoomBookingController {

    private final RoomBookingService roomBookingService;

    //  Obtener todas
    @GetMapping
    public ResponseEntity<List<RoomBookingResponse>> getAll() {
        return ResponseEntity.ok(roomBookingService.findAll());
    }

    //  Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<RoomBookingResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(roomBookingService.findById(id));
    }

    // Crear
    @PostMapping
    public ResponseEntity<RoomBookingResponse> create(@RequestBody @Valid RoomBookingRequest request) {

        RoomBookingResponse response = roomBookingService.create(request);

        return ResponseEntity
                .created(URI.create("/api/v1/room-bookings/" + response.id()))
                .body(response);
    }

    //  Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<RoomBookingResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid RoomBookingRequest request) {

        return ResponseEntity.ok(roomBookingService.update(id, request));
    }

    //  Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        roomBookingService.delete(id);

        return ResponseEntity.noContent().build();
    }

    // Buscar por cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<RoomBookingResponse>> getByClienteId(@PathVariable Long clienteId) {
        return ResponseEntity.ok(roomBookingService.findByClienteId(clienteId));
    }

    // Buscar por habitación
    @GetMapping("/habitacion/{habitacionId}")
    public ResponseEntity<List<RoomBookingResponse>> getByHabitacionId(@PathVariable Long habitacionId) {
        return ResponseEntity.ok(roomBookingService.findByHabitacionId(habitacionId));
    }
}
