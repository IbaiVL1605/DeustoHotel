package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.*;
import com.example.deusto_hotel.model.CourtType;
import com.example.deusto_hotel.service.CourtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controlador REST encargado de la gestión de pistas deportivas
 * y consultas de disponibilidad.
 * <p>
 * Expone endpoints para consultar disponibilidad semanal,
 * disponibilidad por fecha y filtrado por tipo de pista.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/courts")
@RequiredArgsConstructor
public class CourtController {

    /**
     * Servicio que contiene la lógica de negocio
     * relacionada con pistas deportivas.
     */
    private final CourtService courtService;

    /**
     * Obtiene las pistas disponibles aplicando filtros opcionales
     * por tipo, fecha y semana.
     * <p>
     * La lógica de filtrado combina los parámetros recibidos
     * para devolver la disponibilidad correspondiente.
     * </p>
     *
     * @param tipo   tipo de pista (opcional)
     * @param fecha  fecha específica (opcional, formato yyyy-MM-dd)
     * @param semana número de semana (opcional)
     * @return lista de disponibilidad de pistas
     */
    @GetMapping("/available")
    public ResponseEntity<List<CourtAvailabilityDTO>> getAvailableCourts(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) Integer semana) {

        List<CourtAvailabilityDTO> result;

        if (tipo != null && !tipo.trim().isEmpty()
                && fecha != null && !fecha.trim().isEmpty()) {

            // Caso: tipo + fecha
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

            // Sin filtros: disponibilidad general por semana
            result = courtService.findAvailableByTypeAndWeek(null, semana);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Obtiene la disponibilidad semanal de pistas deportivas
     * para un mes concreto.
     *
     * @param year  año de consulta
     * @param month mes de consulta
     * @param tipo  tipo de pista (opcional)
     * @return disponibilidad semanal agrupada por días
     */
    @GetMapping("/weekly-availability")
    public ResponseEntity<List<WeekAvailability>> getWeeklyAvailability(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false) String tipo) {

        CourtType courtType = null;

        if (tipo != null && !tipo.trim().isEmpty()) {

            try {
                courtType = CourtType.valueOf(tipo.toUpperCase());
            } catch (Exception ignored) {
                // Si el tipo no es válido, se mantiene null
            }
        }

        List<WeekAvailability> availability = courtService.findWeeklyAvailability(year, month, courtType);

        return ResponseEntity.ok(availability);
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<CourtResponse> blockCourt(@PathVariable Long id) {
        CourtResponse response = courtService.blockCourt(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CourtResponse>> getAll(@RequestParam(required = false) String tipo) {
        List<CourtResponse> courts = courtService.findAll();
        if (tipo != null && !tipo.trim().isEmpty()) {
            courts = courts.stream().filter(c -> c.tipo().name().equalsIgnoreCase(tipo)).toList();
        }
        return ResponseEntity.ok(courts);
    }

    /*
     * @GetMapping("/{id}")
     * public ResponseEntity<CourtResponse> getById(@PathVariable Long id) {
     * throw new UnsupportedOperationException();
     * }
     * 
     * @PostMapping
     * public ResponseEntity<CourtResponse> create(@RequestBody @Valid CourtRequest
     * request) {
     * throw new UnsupportedOperationException();
     * }
     * 
     * @PutMapping("/{id}")
     * public ResponseEntity<CourtResponse> update(
     * 
     * @PathVariable Long id,
     * 
     * @RequestBody @Valid CourtRequest request) {
     * throw new UnsupportedOperationException();
     * }
     * 
     * @DeleteMapping("/{id}")
     * public ResponseEntity<Void> delete(@PathVariable Long id) {
     * throw new UnsupportedOperationException();
     * }
     */
}