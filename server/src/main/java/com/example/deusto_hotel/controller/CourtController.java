package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.*;
import com.example.deusto_hotel.model.CourtType;
import com.example.deusto_hotel.service.CourtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST encargado de la gestión de pistas deportivas
 * y consultas de disponibilidad.
 * <p>
 * Expone endpoints para consultar disponibilidad semanal,
 * disponibilidad por fecha y filtrado por tipo de pista.
 * </p>
 *
 * @author Deusto Hotel Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/courts")
@RequiredArgsConstructor
@Tag(
        name = "Pistas deportivas",
        description = "Endpoints para la gestión y consulta de disponibilidad de pistas deportivas"
)
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
    @Operation(
            summary = "Obtener pistas disponibles",
            description = "Retorna la disponibilidad de pistas deportivas aplicando filtros opcionales " +
                    "por tipo de pista, fecha específica o semana."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Disponibilidad obtenida correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CourtAvailabilityDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros inválidos"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<List<CourtAvailabilityDTO>> getAvailableCourts(

            @Parameter(
                    description = "Tipo de pista a filtrar",
                    required = false,
                    example = "PADEL"
            )
            @RequestParam(required = false) String tipo,

            @Parameter(
                    description = "Fecha específica para consultar disponibilidad. Formato yyyy-MM-dd",
                    required = false,
                    example = "2026-06-15"
            )
            @RequestParam(required = false) String fecha,

            @Parameter(
                    description = "Semana a consultar (1 o 2)",
                    required = false,
                    example = "1"
            )
            @RequestParam(required = false) Integer semana) {

        try {
            MDC.put("endpoint", "GET /api/v1/courts/available");
            MDC.put("courtType", tipo != null ? tipo : "ALL");
            MDC.put("fecha", fecha != null ? fecha : "NONE");
            MDC.put("week", semana != null ? String.valueOf(semana) : "CURRENT");

            log.info("Solicitud de disponibilidad: tipo={}, fecha={}, semana={}", tipo, fecha, semana);

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

            log.info("Respuesta HTTP 200 enviada - {} tipos de pista encontrados", result.size());

            return ResponseEntity.ok(result);
        } finally {
            MDC.clear();
        }
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
    @Operation(
            summary = "Obtener disponibilidad semanal",
            description = "Retorna la disponibilidad semanal de pistas deportivas agrupada por días " +
                    "para un año y mes concretos."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Disponibilidad semanal obtenida correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = WeekAvailability.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros inválidos"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<List<WeekAvailability>> getWeeklyAvailability(

            @Parameter(
                    description = "Año de consulta",
                    required = true,
                    example = "2026"
            )
            @RequestParam int year,

            @Parameter(
                    description = "Mes de consulta",
                    required = true,
                    example = "6"
            )
            @RequestParam int month,

            @Parameter(
                    description = "Tipo de pista",
                    required = false,
                    example = "TENIS"
            )
            @RequestParam(required = false) String tipo) {

        try {
            MDC.put("endpoint", "GET /api/v1/courts/weekly-availability");
            MDC.put("year", String.valueOf(year));
            MDC.put("month", String.valueOf(month));
            MDC.put("courtType", tipo != null ? tipo : "ALL");

            log.info("Solicitud de disponibilidad semanal: year={}, month={}, tipo={}", year, month, tipo);

            CourtType courtType = null;

            if (tipo != null && !tipo.trim().isEmpty()) {

                try {
                    courtType = CourtType.valueOf(tipo.toUpperCase());
                } catch (Exception ignored) {
                    log.warn("Tipo de pista inválido: {}", tipo);
                }
            }

            List<WeekAvailability> availability = courtService.findWeeklyAvailability(year, month, courtType);

            log.info("Respuesta HTTP 200 enviada - {} semana(s) procesadas", availability.size());

            return ResponseEntity.ok(availability);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Bloquea una pista deportiva para impedir nuevas reservas.
     *
     * @param id identificador único de la pista
     * @return pista bloqueada actualizada
     */
    @PostMapping("/{id}/block")
    @Operation(
            summary = "Bloquear pista",
            description = "Bloquea una pista deportiva para impedir que pueda reservarse."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pista bloqueada correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CourtResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pista no encontrada"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<CourtResponse> blockCourt(

            @Parameter(
                    description = "ID de la pista a bloquear",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {

        try {
            MDC.put("endpoint", "POST /api/v1/courts/{id}/block");
            MDC.put("courtId", String.valueOf(id));

            log.info("Solicitud de bloqueo de pista: id={}", id);

            CourtResponse response = courtService.blockCourt(id);

            log.info("Respuesta HTTP 200 enviada - Pista bloqueada");

            return ResponseEntity.ok(response);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Desbloquea una pista deportiva para permitir reservas nuevamente.
     *
     * @param id identificador único de la pista
     * @return pista desbloqueada actualizada
     */
    @PostMapping("/{id}/unblock")
    @Operation(
            summary = "Desbloquear pista",
            description = "Desbloquea una pista deportiva para permitir nuevas reservas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pista desbloqueada correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CourtResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pista no encontrada"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<CourtResponse> unblockCourt(

            @Parameter(
                    description = "ID de la pista a desbloquear",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {

        try {
            MDC.put("endpoint", "POST /api/v1/courts/{id}/unblock");
            MDC.put("courtId", String.valueOf(id));

            log.info("Solicitud de desbloqueo de pista: id={}", id);

            CourtResponse response = courtService.unblockCourt(id);

            log.info("Respuesta HTTP 200 enviada - Pista desbloqueada");

            return ResponseEntity.ok(response);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Obtiene todas las pistas registradas en el sistema.
     * <p>
     * Permite aplicar un filtro opcional por tipo de pista.
     * </p>
     *
     * @param tipo tipo de pista (opcional)
     * @return lista de pistas registradas
     */
    @GetMapping
    @Operation(
            summary = "Obtener todas las pistas",
            description = "Retorna todas las pistas registradas en el sistema, " +
                    "permitiendo filtrar opcionalmente por tipo."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de pistas obtenido correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CourtResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<List<CourtResponse>> getAll(

            @Parameter(
                    description = "Tipo de pista para filtrar resultados",
                    required = false,
                    example = "FUTBOL"
            )
            @RequestParam(required = false) String tipo) {

        try {
            MDC.put("endpoint", "GET /api/v1/courts");
            MDC.put("courtType", tipo != null ? tipo : "ALL");

            log.info("Solicitud de obtención de todas las pistas: tipo={}", tipo);

            List<CourtResponse> courts = courtService.findAll();

            if (tipo != null && !tipo.trim().isEmpty()) {
                courts = courts.stream().filter(c -> c.tipo().name().equalsIgnoreCase(tipo)).toList();
            }

            log.info("Respuesta HTTP 200 enviada - {} pista(s) total(es) filtradas", courts.size());

            return ResponseEntity.ok(courts);
        } finally {
            MDC.clear();
        }
    }
}