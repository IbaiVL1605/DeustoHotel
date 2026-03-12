package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.dto.RoomBookingResponse;
import com.example.deusto_hotel.service.impl.RoomBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/room-bookings")
@RequiredArgsConstructor
public class RoomBookingController {

    private final RoomBookingService roomBookingService;

    @GetMapping
    public ResponseEntity<List<RoomBookingResponse>> getAll() {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomBookingResponse> getById(@PathVariable Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @PostMapping
    public ResponseEntity<RoomBookingResponse> create(@RequestBody @Valid RoomBookingRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomBookingResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid RoomBookingRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<RoomBookingResponse>> getByClienteId(@PathVariable Long clienteId) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @GetMapping("/habitacion/{habitacionId}")
    public ResponseEntity<List<RoomBookingResponse>> getByHabitacionId(@PathVariable Long habitacionId) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }
}
