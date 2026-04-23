package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.*;
import com.example.deusto_hotel.model.CourtType;
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
            @RequestParam int month,
            @RequestParam(required = false) String tipo
    ) {
        CourtType courtType = null;
        if (tipo != null && !tipo.trim().isEmpty()) {
            try {
                courtType = CourtType.valueOf(tipo.toUpperCase());
            } catch (Exception ignored) {}
        }
        List<WeekAvailability> availability = courtService.findWeeklyAvailability(year, month, courtType);
        return ResponseEntity.ok(availability);
    }

    /*
    @GetMapping
    public ResponseEntity<List<CourtResponse>> getAll(@RequestParam(required = false) String tipo) {
        List<CourtResponse> courts = courtService.findAll();
        if (tipo != null && !tipo.trim().isEmpty()) {
            courts = courts.stream().filter(c -> c.tipo().name().equalsIgnoreCase(tipo)).toList();
        }
        return ResponseEntity.ok(courts);
    }


        @GetMapping("/{id}")
    public ResponseEntity<CourtResponse> getById(@PathVariable Long id) {
        // Implementar
        throw new UnsupportedOperationException();
    }

    @PostMapping
    public ResponseEntity<CourtResponse> create(@RequestBody @Valid CourtRequest request) {
        // Implementar
        throw new UnsupportedOperationException();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourtResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid CourtRequest request) {
        // Implementar
        throw new UnsupportedOperationException();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // Implementar
        throw new UnsupportedOperationException();
    }
     */
}
