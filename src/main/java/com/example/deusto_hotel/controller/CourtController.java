package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.CourtDayAvailability;
import com.example.deusto_hotel.dto.CourtRequest;
import com.example.deusto_hotel.dto.CourtResponse;
import com.example.deusto_hotel.dto.WeekAvailability;
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
    public ResponseEntity<?> getAvailableCourts(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) String horaInicio,
            @RequestParam(required = false) String horaFin
    ) {
        if (tipo != null && fecha != null && horaInicio == null && horaFin == null) {
            List<CourtDayAvailability> courts = courtService.findCourtDayAvailability(tipo, fecha);
            return ResponseEntity.ok(courts);
        } else if (tipo != null && fecha != null && horaInicio != null && horaFin != null) {
            List<CourtDayAvailability> courts = courtService.findCourtDayAvailabilityWithRange(tipo, fecha, horaInicio, horaFin);
            return ResponseEntity.ok(courts);
        } else {
            List<CourtResponse> courts = courtService.findAvailableCourts(tipo, fecha, horaInicio, horaFin);
            return ResponseEntity.ok(courts);
        }
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
