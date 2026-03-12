package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.CourtRequest;
import com.example.deusto_hotel.dto.CourtResponse;
import com.example.deusto_hotel.service.impl.CourtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/courts")
@RequiredArgsConstructor
public class CourtController {

    private final CourtService courtService;

    @GetMapping("/available")
    public ResponseEntity<List<CourtResponse>> getAvailableCourts(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) String horaInicio,
            @RequestParam(required = false) String horaFin
    ) {
        List<CourtResponse> courts = courtService.findAvailableCourts(tipo, fecha, horaInicio, horaFin);
        return ResponseEntity.ok(courts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourtResponse> getById(@PathVariable Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @PostMapping
    public ResponseEntity<CourtResponse> create(@RequestBody @Valid CourtRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourtResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid CourtRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }
}
