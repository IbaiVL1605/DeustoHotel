package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.CourtBookingRequest;
import com.example.deusto_hotel.dto.CourtBookingResponse;
import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.service.CourtBookingService;
import jakarta.servlet.http.HttpSession;
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

    // 🔹 GET ALL
    @GetMapping
    public ResponseEntity<List<CourtBookingResponse>> getAll() {
        return ResponseEntity.ok(courtBookingService.findAll());
    }

    // 🔹 GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<CourtBookingResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(courtBookingService.findById(id));
    }

    // 🔹 CREATE
    @PostMapping
    public ResponseEntity<CourtBookingResponse> create(
            @RequestBody @Valid CourtBookingRequest request, HttpSession session) {

        CourtBookingResponse response = courtBookingService.create(request, session);

        // 🔥 REST correcto → 201 CREATED
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 🔹 UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<CourtBookingResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid CourtBookingRequest request) {

        CourtBookingResponse response = courtBookingService.update(id, request);
        return ResponseEntity.ok(response);
    }

    // 🔹 DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courtBookingService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }

    // 🔹 GET BY CLIENTE
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CourtBookingResponse>> getByClienteId(
            @PathVariable Long clienteId) {

        return ResponseEntity.ok(
                courtBookingService.findByClienteId(clienteId)
        );
    }

    // 🔹 GET BY PISTA
    @GetMapping("/pista/{pistaId}")
    public ResponseEntity<List<CourtBookingResponse>> getByPistaId(
            @PathVariable Long pistaId) {

        return ResponseEntity.ok(
                courtBookingService.findByPistaId(pistaId)
        );
    }
}