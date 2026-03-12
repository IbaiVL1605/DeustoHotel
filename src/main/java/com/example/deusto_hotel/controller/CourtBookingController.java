package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.CourtBookingRequest;
import com.example.deusto_hotel.dto.CourtBookingResponse;
import com.example.deusto_hotel.service.CourtBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/court-bookings")
@RequiredArgsConstructor
public class CourtBookingController {

    private final CourtBookingService courtBookingService;

    @GetMapping
    public ResponseEntity<List<CourtBookingResponse>> getAll() {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourtBookingResponse> getById(@PathVariable Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @PostMapping
    public ResponseEntity<CourtBookingResponse> create(@RequestBody @Valid CourtBookingRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourtBookingResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid CourtBookingRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CourtBookingResponse>> getByClienteId(@PathVariable Long clienteId) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @GetMapping("/pista/{pistaId}")
    public ResponseEntity<List<CourtBookingResponse>> getByPistaId(@PathVariable Long pistaId) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }
}
