package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.CourtBookingRequest;
import com.example.deusto_hotel.dto.CourtBookingResponse;
import com.example.deusto_hotel.service.CourtBookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controlador REST encargado de la gestión de reservas de pistas.
 * <p>
 * Expone endpoints para crear, actualizar, eliminar y consultar
 * reservas de pistas deportivas.
 * </p>
 *
 * @author Deusto Hotel Team
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/court-bookings")
@RequiredArgsConstructor
@Tag(
        name = "Reservas de pistas",
        description = "Endpoints para la gestión de reservas de pistas deportivas"
)
public class CourtBookingController {

    /**
     * Servicio encargado de la lógica de negocio
     * de reservas de pistas.
     */
    private final CourtBookingService courtBookingService;

    /*
     * // GET ALL
     * 
     * @GetMapping
     * public ResponseEntity<List<CourtBookingResponse>> getAll() {
     * return ResponseEntity.ok(courtBookingService.findAll());
     * }
     * 
     * // GET BY ID
     * 
     * @GetMapping("/{id}")
     * public ResponseEntity<CourtBookingResponse> getById(@PathVariable Long id) {
     * return ResponseEntity.ok(courtBookingService.findById(id));
     * }
     */

    /**
     * Crea una nueva reserva de pista.
     *
     * @param request datos de la reserva
     * @param session sesión HTTP del usuario
     * @return reserva creada con estado HTTP 201
     */
    @PostMapping
    @Operation(
            summary = "Crear una reserva de pista",
            description = "Crea una nueva reserva de pista deportiva con los datos proporcionados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Reserva creada correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CourtBookingResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de reserva inválidos o pista no disponible"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cliente o pista no encontrados"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<CourtBookingResponse> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos necesarios para crear la reserva",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CourtBookingRequest.class))
            )
            @RequestBody @Valid CourtBookingRequest request,
            HttpSession session) {

        // Añadir requestId y contexto a MDC
        MDC.put("requestId", UUID.randomUUID().toString());
        MDC.put("clienteId", String.valueOf(request.clienteId()));
        MDC.put("pistaId", String.valueOf(request.pistaId()));

        try {
            log.info("Recibiendo petición de creación de reserva");
            CourtBookingResponse response = courtBookingService.create(request, session);
            log.info("Petición de creación procesada");

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        } finally {
            MDC.remove("requestId");
            MDC.remove("clienteId");
            MDC.remove("pistaId");
        }
    }

    /**
     * Actualiza una reserva existente.
     *
     * @param id      identificador de la reserva
     * @param request nuevos datos de la reserva
     * @return reserva actualizada
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar una reserva",
            description = "Actualiza los datos de una reserva existente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reserva actualizada correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CourtBookingResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o pista no disponible"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reserva o pista no encontrada"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<CourtBookingResponse> update(

            @Parameter(
                    description = "ID de la reserva a actualizar",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos de la reserva",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CourtBookingRequest.class))
            )
            @RequestBody @Valid CourtBookingRequest request) {

        MDC.put("requestId", UUID.randomUUID().toString());
        MDC.put("bookingId", String.valueOf(id));

        try {
            log.info("Recibiendo petición de actualización de reserva");
            CourtBookingResponse response = courtBookingService.update(id, request);
            log.info("Petición de actualización procesada");

            return ResponseEntity.ok(response);
        } finally {
            MDC.remove("requestId");
            MDC.remove("bookingId");
        }
    }

    /**
     * Elimina una reserva de pista.
     *
     * @param id identificador de la reserva
     * @return respuesta sin contenido (HTTP 204)
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar una reserva",
            description = "Elimina una reserva de pista existente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Reserva eliminada correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reserva no encontrada"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<Void> delete(

            @Parameter(
                    description = "ID de la reserva a eliminar",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {

        MDC.put("requestId", UUID.randomUUID().toString());
        MDC.put("bookingId", String.valueOf(id));

        try {
            log.info("Recibiendo petición de eliminación de reserva");
            courtBookingService.delete(id);
            log.info("Petición de eliminación procesada");

            return ResponseEntity.noContent().build();
        } finally {
            MDC.remove("requestId");
            MDC.remove("bookingId");
        }
    }

    /**
     * Obtiene todas las reservas de un cliente.
     *
     * @param clienteId identificador del cliente
     * @return lista de reservas del cliente
     */
    @GetMapping("/cliente/{clienteId}")
    @Operation(
            summary = "Obtener reservas por cliente",
            description = "Retorna todas las reservas asociadas a un cliente específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservas obtenidas correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CourtBookingResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cliente no encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<List<CourtBookingResponse>> getByClienteId(

            @Parameter(
                    description = "ID del cliente",
                    required = true,
                    example = "5"
            )
            @PathVariable Long clienteId) {

        MDC.put("requestId", UUID.randomUUID().toString());
        MDC.put("clienteId", String.valueOf(clienteId));

        try {
            log.info("Recibiendo petición para obtener reservas de cliente");
            List<CourtBookingResponse> reservas = courtBookingService.findByClienteId(clienteId);
            log.info("Se procesó la petición de reservas por cliente. Total devuelto: {}", reservas.size());

            return ResponseEntity.ok(reservas);
        } finally {
            MDC.remove("requestId");
            MDC.remove("clienteId");
        }
    }

    /*
     * // GET BY PISTA
     *
     * @GetMapping("/pista/{pistaId}")
     * public ResponseEntity<List<CourtBookingResponse>> getByPistaId(
     *
     * @PathVariable Long pistaId) {
     *
     * return ResponseEntity.ok(
     * courtBookingService.findByPistaId(pistaId)
     * );
     * }
     */

    /**
     * Obtiene todas las reservas registradas.
     *
     * @return lista de reservas
     */
    @GetMapping
    @Operation(
            summary = "Obtener todas las reservas",
            description = "Retorna todas las reservas de pistas registradas en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listado de reservas obtenido correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CourtBookingResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<List<CourtBookingResponse>> getAll() {
        MDC.put("requestId", UUID.randomUUID().toString());

        try {
            log.info("Recibiendo petición para obtener todas las reservas");
            List<CourtBookingResponse> reservas = courtBookingService.findAll();
            log.info("Se procesó la petición de listado total de reservas. Total devuelto: {}", reservas.size());

            return ResponseEntity.ok(reservas);
        } finally {
            MDC.remove("requestId");
        }
    }

    /**
     * Obtiene una reserva concreta mediante su identificador.
     *
     * @param id identificador de la reserva
     * @return reserva encontrada
     */
    @Operation(
            summary = "Obtener reserva por ID",
            description = "Retorna una reserva específica a partir de su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reserva encontrada correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CourtBookingResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reserva no encontrada"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CourtBookingResponse> getById(

            @Parameter(
                    description = "ID de la reserva",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {
        MDC.put("requestId", UUID.randomUUID().toString());
        MDC.put("bookingId", String.valueOf(id));

        try {
            log.info("Recibiendo petición para obtener reserva por ID");
            CourtBookingResponse response = courtBookingService.findById(id);
            log.info("Petición de obtención por ID procesada");

            return ResponseEntity.ok(response);
        } finally {
            MDC.remove("requestId");
            MDC.remove("bookingId");
        }

    }

    @PostMapping("/validar")
    public ResponseEntity<String> validarReserva(
            @RequestParam Long idReserva,
            @RequestParam Long idRecepcionista) {
        MDC.put("requestId", UUID.randomUUID().toString());
        MDC.put("bookingId", String.valueOf(idReserva));
        MDC.put("recepcionistaId", String.valueOf(idRecepcionista));

        try {
            log.info("Recibiendo petición para validar reserva");
            courtBookingService.validarReserva(idReserva, idRecepcionista);
            log.info("Petición de validación procesada");
            return ResponseEntity.ok("Reserva validada correctamente");
        } finally {
            MDC.remove("requestId");
            MDC.remove("bookingId");
            MDC.remove("recepcionistaId");
        }
    }

    /**
     * Cancela una reserva de pista desde administración.
     *
     * @param id identificador de la reserva
     * @return respuesta vacía con código HTTP 200
     */
    @PostMapping("/{id}/cancel")
    @Operation(
            summary = "Cancelar una reserva",
            description = "Cancela una reserva de pista desde administración."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reserva cancelada correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reserva no encontrada"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor"
            )
    })
    public ResponseEntity<Void> cancelBookingAdmin(

            @Parameter(
                    description = "ID de la reserva a cancelar",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {

        MDC.put("requestId", UUID.randomUUID().toString());
        MDC.put("bookingId", String.valueOf(id));

        try {
            log.info("Recibiendo petición de cancelación por admin");
            courtBookingService.cancelBookingAdmin(id);
            log.info("Petición de cancelación procesada");

            return ResponseEntity.ok().build();
        } finally {
            MDC.remove("requestId");
            MDC.remove("bookingId");
        }
    }
}