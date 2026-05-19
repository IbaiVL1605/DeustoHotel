package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.dto.RoomBookingResponse;
import com.example.deusto_hotel.mapper.RoomBookingMapper;
import com.example.deusto_hotel.model.*;
import com.example.deusto_hotel.repository.RoomBookingRepository;
import com.example.deusto_hotel.repository.UserRepository;
import com.example.deusto_hotel.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de reservas de habitaciones del hotel.
 *
 * Proporciona métodos para crear, eliminar y consultar reservas de habitaciones.
 * Gestiona la lógica de negocio para validar fechas, disponibilidad de habitaciones,
 * y la existencia de clientes antes de procesar las reservas.
 *
 * @author Deusto Hotel Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RoomBookingService {

    private final RoomBookingRepository roomBookingRepository;
    private final RoomBookingMapper roomBookingMapper;
    private final RoomRepository roomRepository;
    private  final UserRepository userRepository;

    /**
     * Crea una lista de reservas de habitaciones para uno o más clientes.
     *
     * Valida las fechas, el tipo de habitación y la disponibilidad antes de guardar las reservas.
     * Para las habitaciones tipo SUITE, se reserva la habitación específica solicitada.
     * Para las habitaciones tipo INDIVIDUAL o DOBLE, se asignan automáticamente habitaciones
     * disponibles del tipo solicitado.
     *
     * Validaciones realizadas:
     * - La fecha de salida debe ser posterior a la fecha de entrada.
     * - La fecha de entrada no puede ser anterior a la fecha actual.
     * - El tipo de habitación debe ser válido (SUITE, INDIVIDUAL o DOBLE).
     * - El cliente debe existir en el sistema.
     * - Debe haber habitaciones disponibles del tipo solicitado para las fechas indicadas.
     * - Para SUITE, no debe haber solapamiento con otras reservas en la misma habitación.
     * - Para INDIVIDUAL/DOBLE, debe haber suficientes habitaciones disponibles según la cantidad solicitada.
     *
     * @param request Lista de objetos {@link RoomBookingRequest} con los datos de las reservas a crear.
     *                Cada solicitud contiene: id_cliente, id_habitacion, tipo, fechaEntrada, fechaSalida y cantidad.
     *
     * @throws IllegalArgumentException Si:
     *         - Las fechas no cumplen con las validaciones (salida no posterior a entrada,
     *           o entrada anterior a la fecha actual).
     *         - El tipo de habitación es nulo o inválido.
     *         - El cliente (id_cliente) no existe en el sistema.
     *         - Para SUITE: No existe habitación con el ID y tipo SUITE especificados,
     *           o hay solapamiento con otras reservas en las fechas indicadas.
     *         - Para INDIVIDUAL/DOBLE: No hay suficientes habitaciones disponibles del tipo
     *           y cantidad solicitadas para las fechas indicadas.
     */
    @Transactional()
    public void create(List<RoomBookingRequest> request) {
        try {
            MDC.put("operationType", "create_bookings");
            MDC.put("requestSize", String.valueOf(request.size()));

            log.info("Procesando creación de {} reservas de habitaciones", request.size());

            for  (RoomBookingRequest roomBookingRequest : request) {
                MDC.put("clienteId", String.valueOf(roomBookingRequest.id_cliente()));
                MDC.put("roomType", roomBookingRequest.tipo() != null ? roomBookingRequest.tipo().toString() : "UNKNOWN");

                validarFechas(roomBookingRequest);

                if (roomBookingRequest.tipo() == null) {
                    log.warn("Tipo de habitación nulo para cliente {}", roomBookingRequest.id_cliente());
                    throw new IllegalArgumentException("Tipo de habitación no válido");
                }

                User cliente = userRepository.findById(roomBookingRequest.id_cliente())
                        .orElseThrow(() -> {
                            log.warn("Cliente {} no encontrado en el sistema", roomBookingRequest.id_cliente());
                            return new IllegalArgumentException("Cliente no encontrado");
                        });

                switch (roomBookingRequest.tipo()) {
                    case INDIVIDUAL, DOBLE -> reservarSimples(roomBookingRequest, cliente);
                    case SUITE -> reservarSuits(roomBookingRequest, cliente);
                }
            }

            log.info("Reservas procesadas exitosamente");
        } finally {
            MDC.clear();
        }
    }

    /**
     * Realiza la reserva de una habitación tipo SUITE.
     *
     * Valida que la habitación SUITE exista y no tenga solapamientos de reservas
     * en el período especificado. Crea la reserva con el precio total basado en
     * el precio por noche de la habitación.
     *
     * @param roomBookingRequest Datos de la solicitud de reserva con ID de habitación y fechas.
     * @param cliente Usuario cliente que realiza la reserva.
     *
     * @throws IllegalArgumentException Si no existe habitación con el ID especificado
     *                                   o si ya hay reservas solapadas para las fechas indicadas.
     */
    private void reservarSuits(RoomBookingRequest roomBookingRequest, User cliente) {
        MDC.put("roomId", String.valueOf(roomBookingRequest.id_habitacion()));

        log.info("Procesando reserva SUITE para cliente {} con habitación {}",
                 cliente.getId(), roomBookingRequest.id_habitacion());

        Optional<Room> habitacion = roomRepository.findByIdAndTipo(roomBookingRequest.id_habitacion(), RoomType.SUITE);
        if(habitacion.isEmpty()) {
            log.warn("Habitación SUITE {} no encontrada para cliente {}",
                     roomBookingRequest.id_habitacion(), cliente.getId());
            throw new IllegalArgumentException("Habitacion no encontrada para tipo SUITE");
        }

        // Verificar solapamientos con otras reservas
        List<RoomBooking> solapamientos = roomBookingRepository.findSolapamientos(
                habitacion.get().getId(),
                roomBookingRequest.fechaEntrada(),
                roomBookingRequest.fechaSalida()
        );

        if (!solapamientos.isEmpty()) {
            log.warn("Solapamiento detectado para habitación SUITE {} en las fechas {}-{}",
                     roomBookingRequest.id_habitacion(),
                     roomBookingRequest.fechaEntrada(),
                     roomBookingRequest.fechaSalida());
            throw new IllegalArgumentException("La habitación ya está reservada para las fechas seleccionadas");
        }

        RoomBooking reserva = new RoomBooking();
        reserva.setCliente(cliente);
        reserva.setHabitacion(habitacion.get());
        reserva.setCheckIn(roomBookingRequest.fechaEntrada());
        reserva.setCheckOut(roomBookingRequest.fechaSalida());
        reserva.setPrecioTotal(habitacion.get().getPrecioPorNoche());

        roomBookingRepository.save(reserva);

        log.info("Reserva SUITE creada exitosamente: cliente={}, habitación={}, checkIn={}, checkOut={}",
                 cliente.getId(), habitacion.get().getId(),
                 roomBookingRequest.fechaEntrada(), roomBookingRequest.fechaSalida());
    }

    /**
     * Valida las fechas de entrada y salida de una solicitud de reserva.
     *
     * Realiza las siguientes validaciones:
     * - La fecha de salida debe ser posterior a la fecha de entrada.
     * - La fecha de entrada no puede ser anterior a la fecha actual.
     *
     * @param roomBookingRequest Solicitud de reserva con las fechas a validar.
     *
     * @throws IllegalArgumentException Si la fecha de salida no es posterior a la entrada,
     *                                   o si la fecha de entrada es anterior a la fecha actual.
     */
    private void validarFechas(RoomBookingRequest roomBookingRequest) {
        if(!roomBookingRequest.fechaSalida().isAfter(roomBookingRequest.fechaEntrada())) {
            log.warn("Validación de fechas fallida: fecha de salida {} no es posterior a fecha de entrada {}",
                     roomBookingRequest.fechaSalida(), roomBookingRequest.fechaEntrada());
            throw new IllegalArgumentException("La fecha de salida debe de ser posterior a la fecha de entrada.");

        } if(roomBookingRequest.fechaEntrada().isBefore(LocalDate.now())) {
            log.warn("Validación de fechas fallida: fecha de entrada {} es anterior a la fecha actual",
                     roomBookingRequest.fechaEntrada());
            throw new IllegalArgumentException("La fecha de entrada no puede ser anterior a la fecha actual.");

        }

    }

    /**
     * Realiza la reserva de una o más habitaciones tipo INDIVIDUAL o DOBLE.
     *
     * Busca habitaciones disponibles del tipo solicitado en el período especificado.
     * Asigna automáticamente habitaciones disponibles (la cantidad solicitada) al cliente.
     * Cada habitación se reserva con el precio total basado en su precio por noche.
     *
     * @param roomBookingRequest Datos de la solicitud con tipo, fechas, cantidad y cliente.
     * @param cliente Usuario cliente que realiza la reserva.
     *
     * @throws IllegalArgumentException Si no hay habitaciones disponibles del tipo solicitado
     *                                   en la cantidad requerida para las fechas indicadas.
     */
    private void reservarSimples(RoomBookingRequest roomBookingRequest, User cliente) {
        MDC.put("roomType", roomBookingRequest.tipo().toString());
        MDC.put("quantity", String.valueOf(roomBookingRequest.cantidad()));

        log.info("Procesando reserva {} para cliente {} con cantidad {}",
                 roomBookingRequest.tipo(), cliente.getId(), roomBookingRequest.cantidad());

        List<Room> disponiblesTotal = roomRepository.findRoomDisponibles(roomBookingRequest.fechaEntrada(), roomBookingRequest.fechaSalida());
        if(disponiblesTotal.isEmpty()) {
            log.warn("No hay habitaciones disponibles para las fechas {}-{}",
                     roomBookingRequest.fechaEntrada(), roomBookingRequest.fechaSalida());
            throw new IllegalArgumentException("No hay habitaciones disponibles para las fechas seleccionadas");
        }

        List<Room> disponibles = disponiblesTotal.stream()
                .filter(h -> h.getTipo().equals(roomBookingRequest.tipo()))
                .toList();


        if(disponibles.size() < roomBookingRequest.cantidad()) {
            log.warn("Cantidad insuficiente de habitaciones {}: solicitadas={}, disponibles={}",
                     roomBookingRequest.tipo(), roomBookingRequest.cantidad(), disponibles.size());
            throw new IllegalArgumentException(String.format("No hay habitaciones disponibles para las fechas seleccionadas con el tipo: %s", roomBookingRequest.tipo()));
        }

        for (int i = 0; i < roomBookingRequest.cantidad(); i++) {
            RoomBooking reserva = new RoomBooking();

            reserva.setCliente(cliente);
            reserva.setCheckIn(roomBookingRequest.fechaEntrada());
            reserva.setCheckOut(roomBookingRequest.fechaSalida());

            Room habitacion = disponibles.get(i);
            reserva.setPrecioTotal(habitacion.getPrecioPorNoche());
            reserva.setHabitacion(habitacion);

            roomBookingRepository.save(reserva);

            log.info("Reserva {} creada exitosamente: cliente={}, habitación={}, checkIn={}, checkOut={}",
                     roomBookingRequest.tipo(), cliente.getId(), habitacion.getId(),
                     roomBookingRequest.fechaEntrada(), roomBookingRequest.fechaSalida());
        }
    }

    public void validarReserva(Long idReserva, Long idRecepcionista) {
        try {
            MDC.put("operationType", "validate_booking");
            MDC.put("bookingId", String.valueOf(idReserva));
            MDC.put("receptionistId", String.valueOf(idRecepcionista));

            log.info("Iniciando validación de reserva {} por recepcionista {}", idReserva, idRecepcionista);

            if (idRecepcionista == null) {
                log.warn("Intento de validación de reserva sin recepcionista autenticado");
                throw new IllegalArgumentException("Usuario no autenticado");
            }

            User recepcionista = userRepository.findById(idRecepcionista)
                    .orElseThrow(() -> {
                        log.warn("Recepcionista {} no encontrado", idRecepcionista);
                        return new IllegalArgumentException("Usuario no encontrado");
                    });

            if (recepcionista.getRol() != Role.RECEPTIONIST) {
                log.warn("Intento de validación por usuario {} sin rol de recepcionista", idRecepcionista);
                throw new IllegalArgumentException("Usuario no autorizado");
            }

            RoomBooking reserva = roomBookingRepository.findById(idReserva)
                    .orElseThrow(() -> {
                        log.warn("Reserva {} no encontrada para validación", idReserva);
                        return new IllegalArgumentException("Reserva no encontrada");
                    });

            if (reserva.getEstado() != RoomBookingStatus.PENDIENTE) {
                log.warn("Intento de validar reserva {} que no está en estado PENDIENTE: {}",
                        idReserva, reserva.getEstado());
                throw new IllegalArgumentException("La reserva no está en estado pendiente");
            }

            reserva.setEstado(RoomBookingStatus.CONFIRMADA);
            roomBookingRepository.save(reserva);

            log.info("Reserva {} validada exitosamente por recepcionista {}", idReserva, idRecepcionista);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Elimina una reserva específica, verificando que el usuario sea el propietario.
     *
     * Realiza validaciones de seguridad:
     * - Verifica que la reserva exista en el sistema.
     * - Verifica que el usuario esté autenticado (userId no nulo).
     * - Verifica que el usuario sea el propietario de la reserva (cliente).
     *
     * Solo el cliente que realizó la reserva puede cancelarla. Los administradores
     * u otros usuarios no pueden cancelar reservas ajenas.
     *
     * @param id Identificador único de la reserva a eliminar.
     * @param userId Identificador del usuario que realiza la eliminación (debe ser el propietario).
     *
     * @throws ResponseStatusException con código 404 (Not Found) si la reserva no existe.
     * @throws ResponseStatusException con código 401 (Unauthorized) si userId es nulo
     *                                   (usuario no autenticado).
     * @throws ResponseStatusException con código 403 (Forbidden) si el userId no coincide
     *                                   con el ID del cliente propietario de la reserva.
     */
    public void delete(Long id, Long userId) {
        try {
            MDC.put("operationType", "delete_booking");
            MDC.put("bookingId", String.valueOf(id));
            MDC.put("userId", String.valueOf(userId));

            log.info("Iniciando eliminación de reserva {} por usuario {}", id, userId);

            RoomBooking booking = roomBookingRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Intento de eliminar reserva {} que no existe", id);
                        return new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Reserva no encontrada");
                    });

            if (userId == null) {
                log.warn("Intento de eliminar reserva sin usuario autenticado");
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
            }

            if (!booking.getCliente().getId().equals(userId)) {
                log.warn("Usuario {} intentó eliminar reserva {} que pertenece a cliente {}",
                         userId, id, booking.getCliente().getId());
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "No puedes cancelar esta reserva");
            }

            roomBookingRepository.deleteById(id);
            log.info("Reserva {} eliminada exitosamente por usuario {}", id, userId);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Obtiene todas las reservas asociadas a un cliente específico.
     *
     * Recupera todas las reservas del cliente ordenadas por fecha de creación
     * o según la lógica del repositorio. Las reservas se mapean a objetos
     * {@link RoomBookingResponse} para ser retornadas.
     *
     * Este método es de solo lectura (readOnly = true) para optimizar
     * el rendimiento en consultas sin necesidad de transacción completa.
     *
     * @param clienteId Identificador único del cliente cuyas reservas se desean obtener.
     *
     * @return Lista de objetos {@link RoomBookingResponse} con todas las reservas del cliente.
     *         Puede retornar una lista vacía si el cliente no tiene reservas.
     */
    @Transactional(readOnly = true)
    public List<RoomBookingResponse> findByClienteId(Long clienteId) {
        try {
            MDC.put("operationType", "find_bookings_by_client");
            MDC.put("clienteId", String.valueOf(clienteId));

            log.info("Buscando reservas para cliente {}", clienteId);

            List<RoomBookingResponse> reservas = roomBookingRepository.findByClienteId(clienteId)
                    .stream()
                    .map(roomBookingMapper::toResponse)
                    .toList();

            log.info("Se encontraron {} reservas para cliente {}", reservas.size(), clienteId);

            return reservas;
        } finally {
            MDC.clear();
        }
    }



    public List<RoomBookingResponse> findAll() {
        try {
            MDC.put("operationType", "find_all_bookings");

            log.info("Obteniendo todas las reservas");

            List<RoomBookingResponse> reservas = roomBookingRepository.findAll()
                    .stream()
                    .map(roomBookingMapper::toResponse)
                    .toList();

            log.info("Total de reservas en el sistema: {}", reservas.size());

            return reservas;
        } finally {
            MDC.clear();
        }
    }

}
