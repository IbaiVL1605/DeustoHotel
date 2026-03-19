package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.*;
import com.example.deusto_hotel.service.CourtService;
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
    public ResponseEntity<List<CourtAvailabilityDTO>> getAvailableCourts(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) Integer semana
    ) {
        List<CourtAvailabilityDTO> result;

        if (tipo != null && !tipo.trim().isEmpty() && fecha != null && !fecha.trim().isEmpty()) {
            // Ambos parámetros: tipo y fecha
            List<CourtAvailabilityDTO> byDate = courtService.findAvailableByDate(fecha);
            result = byDate.stream()
                .filter(dto -> dto.tipo().name().equalsIgnoreCase(tipo))
                .toList();
        } else if (tipo != null && !tipo.trim().isEmpty()) {
            // Solo tipo
            result = courtService.findAvailableByTypeAndWeek(tipo, semana);
        } else if (fecha != null && !fecha.trim().isEmpty()) {
            // Solo fecha
            result = courtService.findAvailableByDate(fecha);
        } else {
            // Ninguno: retornar todas las disponibilidades de la semana
            result = courtService.findAvailableByTypeAndWeek(null, semana);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/weekly-availability")
    public ResponseEntity<List<WeekAvailability>> getWeeklyAvailability(
            @RequestParam int year,
            @RequestParam int month
    ) {
        List<WeekAvailability> availability = courtService.findWeeklyAvailability(year, month, null);
        return ResponseEntity.ok(availability);
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
