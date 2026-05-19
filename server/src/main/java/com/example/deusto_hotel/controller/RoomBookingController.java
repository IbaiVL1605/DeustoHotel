package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.dto.RoomBookingResponse;
import com.example.deusto_hotel.service.RoomBookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/room-bookings")
@RequiredArgsConstructor
@Tag(name = "Room Booking", description = "Gestión de reservas de habitaciones del hotel")
@Slf4j
public class RoomBookingController {

    private final RoomBookingService roomBookingService;

    /**
     * Crea una lista de reservas de habitaciones para uno o múltiples clientes.
     *
     * Valida todas las solicitudes de reserva before processing, validando:
     * - Las fechas de entrada y salida son válidas (salida posterior a entrada, entrada no anterior a hoy)
     * - El tipo de habitación es válido (SUITE, INDIVIDUAL o DOBLE)
     * - El cliente existe en el sistema
     * - Para SUITE: La habitación específica existe y no tiene conflictos de disponibilidad
     * - Para INDIVIDUAL/DOBLE: Hay suficientes habitaciones disponibles del tipo solicitado
     *
     * Si alguna validación falla, se lanzará una {@link IllegalArgumentException} que será
     * convertida a una respuesta HTTP 400 (Bad Request).
     *
     * @param request Lista de objetos {@link RoomBookingRequest} que contienen los datos de las
     *                habitaciones a reservar. Cada solicitud debe incluir: id_cliente, id_habitacion,
     *                tipo, fechaEntrada, fechaSalida y cantidad.
     *
     * @return {@link ResponseEntity} con código HTTP 201 (Created).
     *         No incluye un cuerpo en la respuesta.
     *
     * @throws IllegalArgumentException Si:
     *         - Las fechas no cumplen validaciones (salida no posterior a entrada,
     *           o entrada anterior a la fecha actual).
     *         - El tipo de habitación es nulo o inválido.
     *         - El cliente (id_cliente) no existe en el sistema.
     *         - Para SUITE: No existe habitación con el ID/tipo especificados,
     *           o hay solapamiento con otras reservas.
     *         - Para INDIVIDUAL/DOBLE: No hay suficientes habitaciones disponibles.
     *         Genera respuesta HTTP 400 (Bad Request).
     *
     * @throws jakarta.validation.ConstraintViolationException Si los parámetros de request
     *                                                         no cumplen con las restricciones
     *                                                         de validación de Jakarta.
     *                                                         Genera respuesta HTTP 400 (Bad Request).
     */
    @PostMapping
    @Operation(
        summary = "Crear reservas de habitaciones",
        description = "Crea una lista de reservas de habitaciones validando disponibilidad, datos de cliente " +
                      "y fechas. Soporta reservas de SUITE (habitación específica) e INDIVIDUAL/DOBLE (asignación automática)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Reservas creadas exitosamente"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Error en la solicitud: fechas inválidas, tipo de habitación inválido, " +
                          "cliente no encontrado, habitación no disponible, o no hay suficientes habitaciones"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public ResponseEntity<Void> create(
            @RequestBody @Valid List<RoomBookingRequest> request
    ) {
        try {
            MDC.put("endpoint", "POST /api/v1/room-bookings");
            MDC.put("requestSize", String.valueOf(request.size()));

            log.info("Solicitud de creación de {} reservas recibida", request.size());

            request.forEach(RoomBookingRequest::validate);

            roomBookingService.create(request);

            log.info("Respuesta HTTP 201 enviada - Reservas creadas");

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } finally {
            MDC.clear();
        }
    }

    /**
     * Elimina una reserva específica, validando que el usuario sea el propietario.
     *
     * Realiza validaciones de seguridad:
     * - Verifica que la reserva exista en el sistema.
     * - Verifica que el usuario esté autenticado (userId no nulo).
     * - Verifica que el usuario sea el cliente propietario de la reserva.
     *
     * Solo el cliente propietario puede cancelar su reserva. Si el ID del usuario
     * no coincide con el del cliente que realizó la reserva, la operación será rechazada
     * con código HTTP 403 (Forbidden).
     *
     * @param id Identificador único de la reserva a eliminar. Debe ser un valor válido y existente.
     *
     * @param userId Identificador del usuario que realiza la eliminación (cliente propietario).
     *               Debe coincidir con el ID del cliente que realizó la reserva.
     *
     * @return {@link ResponseEntity} con código HTTP 204 (No Content).
     *         No incluye un cuerpo en la respuesta.
     *
     * @throws ResponseStatusException con código 404 (Not Found) si la reserva con el ID
     *                                   especificado no existe.
     * @throws ResponseStatusException con código 401 (Unauthorized) si userId es nulo
     *                                   (usuario no autenticado).
     * @throws ResponseStatusException con código 403 (Forbidden) si el userId no coincide
     *                                   con el ID del cliente propietario de la reserva.
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar una reserva de habitación",
        description = "Elimina una reserva específica verificando que el usuario autenticado sea el propietario/cliente. " +
                      "Solo el cliente que realizó la reserva puede cancelarla."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Reserva eliminada exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Error: Reserva no encontrada"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Error: Usuario no autenticado"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Error: No tienes permisos para cancelar esta reserva (no eres el propietario)"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public ResponseEntity<Void> delete(
            @Parameter(
                description = "ID único de la reserva a eliminar",
                required = true,
                example = "1"
            )
            @PathVariable Long id,

            @Parameter(
                description = "ID del usuario (cliente) que realiza la eliminación. " +
                              "Debe ser el propietario de la reserva",
                required = true,
                example = "123"
            )
            @RequestParam Long userId
    ) {
        try {
            MDC.put("endpoint", "DELETE /api/v1/room-bookings/{id}");
            MDC.put("bookingId", String.valueOf(id));
            MDC.put("userId", String.valueOf(userId));

            log.info("Solicitud de eliminación de reserva {} por usuario {}", id, userId);

            roomBookingService.delete(id, userId);

            log.info("Respuesta HTTP 204 enviada - Reserva eliminada");

            return ResponseEntity.noContent().build();
        } finally {
            MDC.clear();
        }
    }

    /**
     * Obtiene todas las reservas asociadas a un cliente específico.
     *
     * Recupera la lista completa de reservas realizadas por el cliente especificado.
     * Las reservas se retornan en un objeto {@link RoomBookingResponse} mapeado desde
     * la entidad interna del modelo.
     *
     * @param clienteId Identificador único del cliente cuyas reservas se desean obtener.
     *
     * @return {@link ResponseEntity} con código HTTP 200 (OK) y el cuerpo contiene una
     *         lista de {@link RoomBookingResponse} con todas las reservas del cliente.
     *         Puede retornar una lista vacía si el cliente no tiene reservas.
     */
    @GetMapping("/cliente/{clienteId}")
    @Operation(
        summary = "Obtener reservas por cliente",
        description = "Retorna todas las reservas de habitaciones realizadas por un cliente específico."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Reservas obtenidas exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomBookingResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public ResponseEntity<List<RoomBookingResponse>> getByClienteId(
            @Parameter(
                description = "ID único del cliente cuyas reservas se desean obtener",
                required = true,
                example = "123"
            )
            @PathVariable Long clienteId
    ) {
        try {
            MDC.put("endpoint", "GET /api/v1/room-bookings/cliente/{clienteId}");
            MDC.put("clienteId", String.valueOf(clienteId));

            log.info("Solicitud de obtención de reservas para cliente {}", clienteId);

            List<RoomBookingResponse> reservas = roomBookingService.findByClienteId(clienteId);

            log.info("Respuesta HTTP 200 enviada - {} reserva(s) encontrada(s)", reservas.size());

            return ResponseEntity.ok(reservas);
        } finally {
            MDC.clear();
        }
    }

	/**
	 * Valida una reserva de habitación existente.
	 * <p>
	 * Cambia el estado de la reserva de PENDIENTE a CONFIRMADA. Solo un recepcionista
	 * autorizado puede validar una reserva. Se valida que la reserva exista y esté
	 * en estado pendiente antes de confirmarla.
	 * </p>
	 *
	 * @param idReserva       identificador de la reserva a validar
	 * @param idRecepcionista identificador del recepcionista que realiza la validación
	 * @return mensaje de éxito indicando que la reserva fue validada correctamente
	 */
	@PostMapping("/validar")
	@Operation(
			summary = "Validar una reserva de habitación",
			description = "Valida una reserva de habitación existente cambiando su estado de PENDIENTE a CONFIRMADA. " +
					"Solo un recepcionista autorizado puede realizar esta operación."
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "Reserva validada correctamente",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(type = "string", example = "Reserva validada correctamente")
					)
			),
			@ApiResponse(
					responseCode = "400",
					description = "Datos inválidos: usuario no autenticado, usuario no encontrado, usuario no autorizado, " +
							"reserva no encontrada o reserva no está en estado pendiente"
			),
			@ApiResponse(
					responseCode = "500",
					description = "Error interno del servidor"
			)
	})
	public ResponseEntity<String> validarReserva(

			@Parameter(
					description = "ID de la reserva a validar",
					required = true,
					example = "1"
			)
			@RequestParam Long idReserva,

			@Parameter(
					description = "ID del recepcionista que valida la reserva",
					required = true,
					example = "5"
			)
			@RequestParam Long idRecepcionista) {
		try {
			MDC.put("endpoint", "POST /api/v1/room-bookings/validar");
			MDC.put("bookingId", String.valueOf(idReserva));
			MDC.put("receptionistId", String.valueOf(idRecepcionista));

			log.info("Solicitud de validación de reserva {} por recepcionista {}", idReserva, idRecepcionista);

			roomBookingService.validarReserva(idReserva, idRecepcionista);

			log.info("Respuesta HTTP 200 enviada - Reserva validada");

			return ResponseEntity.ok("Reserva validada correctamente");
		} finally {
			MDC.clear();
		}
	}

    /*
    // Buscar por habitación
    @GetMapping("/habitacion/{habitacionId}")
    public ResponseEntity<List<RoomBookingResponse>> getByHabitacionId(@PathVariable Long habitacionId) {
        return ResponseEntity.ok(roomBookingService.findByHabitacionId(habitacionId));
    }
    */

	/**
	 * Obtiene todas las reservas de habitaciones del sistema.
	 * <p>
	 * Retorna una lista completa con todas las reservas registradas, sin aplicar
	 * ningún tipo de filtro. Cada reserva incluye información del cliente, habitación,
	 * fechas de entrada/salida, estado y precio total.
	 * </p>
	 *
	 * @return lista completa de todas las reservas del sistema
	 */
	@GetMapping
	@Operation(
			summary = "Obtener todas las reservas de habitaciones",
			description = "Retorna todas las reservas de habitaciones registradas en el sistema sin aplicar filtros."
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "Listado de reservas obtenido correctamente",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = RoomBookingResponse.class)
					)
			),
			@ApiResponse(
					responseCode = "500",
					description = "Error interno del servidor"
			)
	})
	public ResponseEntity<List<RoomBookingResponse>> getAll() {
		try {
			MDC.put("endpoint", "GET /api/v1/room-bookings");

			log.info("Solicitud de obtención de todas las reservas");

			List<RoomBookingResponse> reservas = roomBookingService.findAll();

			log.info("Respuesta HTTP 200 enviada - {} reserva(s) total(es)", reservas.size());

			return ResponseEntity.ok(reservas);
		} finally {
			MDC.clear();
		}
	}

}
