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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RestController
@RequestMapping("/api/v1/court-bookings")
@RequiredArgsConstructor
@Tag(
        name = "Reservas de pistas",
        description = "Endpoints para la gestión de reservas de pistas deportivas"
)
public class CourtBookingController {

    private static final Logger log = LoggerFactory.getLogger(CourtBookingController.class);
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

        CourtBookingResponse response = courtBookingService.create(request, session);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
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

        CourtBookingResponse response = courtBookingService.update(id, request);

        return ResponseEntity.ok(response);
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

        courtBookingService.delete(id);

        return ResponseEntity.noContent().build();
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

        return ResponseEntity.ok(
                courtBookingService.findByClienteId(clienteId));
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

        return ResponseEntity.ok(courtBookingService.findAll());
    }

    /**
     * Obtiene una reserva concreta mediante su identificador.
     *
     * @param id identificador de la reserva
     * @return reserva encontrada
     */
    @GetMapping("/{id}")
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
    public ResponseEntity<CourtBookingResponse> getById(

            @Parameter(
                    description = "ID de la reserva",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {

        return ResponseEntity.ok(courtBookingService.findById(id));
    }

    @PostMapping("/validar")
    public ResponseEntity<String> validarReserva(
            @RequestParam Long idReserva,
            @RequestParam Long idRecepcionista) {
        courtBookingService.validarReserva(idReserva, idRecepcionista);
        log.info("Reserva con ID {} validada por recepcionista con ID {}", idReserva, idRecepcionista);
        return ResponseEntity.ok("Reserva validada correctamente");
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

        courtBookingService.cancelBookingAdmin(id);

        return ResponseEntity.ok().build();
    }
}