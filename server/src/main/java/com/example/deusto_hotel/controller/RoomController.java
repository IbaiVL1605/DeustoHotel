package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.RoomDisponibleResponse;
import com.example.deusto_hotel.dto.RoomRequest;
import com.example.deusto_hotel.dto.RoomResponse;
import com.example.deusto_hotel.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para la gestión de habitaciones del hotel.
 *
 * Proporciona endpoints para crear, eliminar y consultar habitaciones,
 * así como para obtener habitaciones disponibles en un rango de fechas.
 *
 * @author Deusto Hotel Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Tag(name = "Habitaciones", description = "Endpoints para la gestión de habitaciones del hotel")
public class RoomController {

    private static final Logger log = LoggerFactory.getLogger(RoomController.class);
    private final RoomService roomService;

    /**
     * Crea una nueva habitación en el sistema.
     *
     * Valida que no exista una habitación con el mismo número y que el tipo
     * de habitación sea válido (SUITE, INDIVIDUAL, DOBLE). Si alguna validación
     * falla, se lanzará una {@link IllegalArgumentException} que será convertida
     * a una respuesta HTTP 400 (Bad Request).
     *
     * @param request Objeto {@link RoomRequest} con los datos de la habitación a crear.
     *                Contiene los campos: numero, tipo, capacidad y precioPorNoche.
     *                Debe cumplir con las validaciones definidas en RoomRequest.
     *
     * @return {@link ResponseEntity} con código HTTP 201 (Created) y el cuerpo contiene
     *         un {@link RoomResponse} con los datos de la habitación creada,
     *         incluyendo su ID asignado automáticamente.
     *
     * @throws IllegalArgumentException Si ya existe una habitación con el número proporcionado,
     *                                   o si el tipo de habitación no es válido.
     *                                   Genera respuesta HTTP 400 (Bad Request).
     *
     * @throws jakarta.validation.ConstraintViolationException Si los parámetros de request
     *                                                         no cumplen con las restricciones
     *                                                         de validación definidas.
     *                                                         Genera respuesta HTTP 400 (Bad Request).
     */
    @PostMapping
    @Operation(
        summary = "Crear una nueva habitación",
        description = "Crea una nueva habitación en el sistema con los datos proporcionados. " +
                      "Valida que no exista una habitación con el mismo número y que el tipo sea válido."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Habitación creada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error en la solicitud: número de habitación duplicado, tipo inválido o validación fallida"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public ResponseEntity<RoomResponse> create(
            @RequestBody @Valid RoomRequest request
    ) {
        MDC.put("endpoint", "POST /api/v1/rooms");
        MDC.put("roomNumber", request.numero());
        MDC.put("roomType", request.tipo().toString());

        try {
            log.info("Solicitud de creación de habitación recibida");

            RoomResponse created = roomService.create(request);

            MDC.put("roomId", String.valueOf(created.id()));

            return ResponseEntity.status(201).body(created);
        } finally {
            MDC.remove("endpoint");
            MDC.remove("roomNumber");
            MDC.remove("roomType");
            MDC.remove("roomId");
        }
    }

    /**
     * Elimina una habitación existente del sistema.
     *
     * Si la habitación con el ID proporcionado no existe, se lanzará una
     * {@link RuntimeException} que será convertida a una respuesta HTTP 400 (Bad Request).
     *
     * @param id Identificador único de la habitación a eliminar. Debe ser un valor positivo.
     *
     * @return {@link ResponseEntity} con código HTTP 204 (No Content).
     *         No incluye un cuerpo en la respuesta.
     *
     * @throws RuntimeException Si la habitación con el ID especificado no existe.
     *                          Genera respuesta HTTP 400 (Bad Request).
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar una habitación",
        description = "Elimina una habitación del sistema por su identificador único."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Habitación eliminada exitosamente"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error: Habitación no encontrada"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        MDC.put("endpoint", "DELETE /api/v1/rooms/{id}");
        MDC.put("roomId", id.toString());

        try {
            log.info("Solicitud de eliminación de habitación recibida");

            roomService.delete(id);

            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            log.warn("Habitación no encontrada para eliminación");
            return ResponseEntity.notFound().build();
        } finally {
            MDC.remove("endpoint");
            MDC.remove("roomId");
        }
    }

    /**
     * Obtiene la lista de habitaciones disponibles en un rango de fechas específico.
     *
     * Devuelve todas las habitaciones que no tienen reservas que se solapen con el
     * rango de fechas proporcionado (desde fechaEntrada hasta fechaSalida).
     *
     * Validaciones:
     * - La fecha de salida debe ser posterior a la fecha de entrada.
     * - La fecha de entrada no puede ser anterior a la fecha actual.
     *
     * Si alguna validación falla, se lanzará una {@link IllegalArgumentException}.
     *
     * @param fechaEntrada Fecha de entrada solicitada (inclusive). Formato: yyyy-MM-dd.
     *                     Debe ser igual o posterior a la fecha actual.
     *
     * @param fechaSalida Fecha de salida solicitada (inclusive). Formato: yyyy-MM-dd.
     *                    Debe ser posterior a fechaEntrada.
     *
     * @return {@link ResponseEntity} con código HTTP 200 (OK) y el cuerpo contiene
     *         una lista de {@link RoomDisponibleResponse} con las habitaciones disponibles
     *         en el rango de fechas especificado. La lista puede estar vacía si no hay
     *         habitaciones disponibles.
     *
     * @throws IllegalArgumentException Si la fecha de salida no es posterior a la fecha de entrada,
     *                                   o si la fecha de entrada es anterior a la fecha actual.
     *                                   Genera respuesta HTTP 400 (Bad Request).
     */
    @GetMapping("/disponibles")
    @Operation(
        summary = "Obtener habitaciones disponibles",
        description = "Retorna una lista de habitaciones disponibles para un rango de fechas especificado. " +
                      "Las habitaciones disponibles son aquellas sin reservas que se solapen en el período indicado."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de habitaciones disponibles obtenida exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomDisponibleResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error en los parámetros: fecha de salida debe ser posterior a entrada, " +
                          "o fecha de entrada no puede ser anterior a la actual"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public ResponseEntity<List<RoomDisponibleResponse>> getByDisponibles(
            @Parameter(
                description = "Fecha de entrada (inclusive). Formato: yyyy-MM-dd. " +
                              "Debe ser igual o posterior a la fecha actual",
                required = true,
                example = "2026-05-15"
            )
            @RequestParam LocalDate fechaEntrada,

            @Parameter(
                description = "Fecha de salida (inclusive). Formato: yyyy-MM-dd. " +
                              "Debe ser posterior a la fecha de entrada",
                required = true,
                example = "2026-05-20"
            )
            @RequestParam LocalDate fechaSalida
    ) {

        MDC.put("endpoint", "GET /api/v1/rooms/disponibles");
        MDC.put("fechaEntrada", fechaEntrada.toString());
        MDC.put("fechaSalida", fechaSalida.toString());

        try {
            log.info("Solicitud de búsqueda de habitaciones disponibles recibida");

            if(!fechaEntrada.isBefore(fechaSalida)) {
                log.warn("Validación fallida - Fecha de salida no es posterior a fecha de entrada");
                throw new IllegalArgumentException("La fecha de salida debe de ser posterior a la fecha de entrada.");

            } else if(fechaEntrada.isBefore(LocalDate.now())) {
                log.warn("Validación fallida - Fecha de entrada es anterior a la actual");
                throw new IllegalArgumentException("La fecha de entrada no puede ser anterior a la fecha actual.");

            }

            List<RoomDisponibleResponse> resultado = roomService.getDisponibles(fechaEntrada, fechaSalida);

            return ResponseEntity.ok(resultado);
        } finally {
            MDC.remove("endpoint");
            MDC.remove("fechaEntrada");
            MDC.remove("fechaSalida");
        }
    }

    @PutMapping("/{id}/bloquear")
    public ResponseEntity<String> bloquearHabitacion(@PathVariable Long id) {
        MDC.put("endpoint", "GET /api/v1/rooms/bloquearHabitacion");
        MDC.put("roomId", id.toString());

        try {
            log.info("Solicitud de bloquear habitacion recibida");

            roomService.bloquearHabitacion(id);

            return ResponseEntity.ok("Habitación bloqueada correctamente");

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        } finally {
            MDC.remove("endpoint");
            MDC.remove("roomId");

        }
    }
}
