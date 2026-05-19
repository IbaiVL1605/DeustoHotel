package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.CourtBookingRequest;
import com.example.deusto_hotel.dto.CourtBookingResponse;
import com.example.deusto_hotel.mapper.CourtBookingMapper;
import com.example.deusto_hotel.model.Court;
import com.example.deusto_hotel.model.CourtBooking;
import com.example.deusto_hotel.model.CourtBookingStatus;
import com.example.deusto_hotel.model.Role;
import com.example.deusto_hotel.model.User;
import com.example.deusto_hotel.repository.CourtBookingRepository;
import com.example.deusto_hotel.repository.CourtRepository;
import com.example.deusto_hotel.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.slf4j.MDC;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * Servicio encargado de la gestión de reservas de pistas deportivas.
 * <p>
 * Permite crear, actualizar, eliminar y consultar reservas
 * asociadas a clientes y pistas.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CourtBookingService {

        /**
         * Repositorio de acceso a datos de reservas de pistas.
         */
        private final CourtBookingRepository courtBookingRepository;

        /**
         * Repositorio de acceso a datos de pistas deportivas.
         */
        private final CourtRepository courtRepository;

        /**
         * Repositorio de acceso a datos de usuarios.
         */
        private final UserRepository userRepository;

        /**
         * Mapper encargado de transformar entidades y DTOs
         * de reservas de pistas.
         */
        private final CourtBookingMapper courtBookingMapper;

        /**
         * Componente utilizado para enviar notificaciones WebSocket
         * a los clientes conectados.
         */
        private final SimpMessagingTemplate messagingTemplate;

        /**
         * Crea una nueva reserva de pista.
         * <p>
         * Calcula automáticamente el precio total en función
         * de las horas reservadas y del precio por hora de la pista.
         * Además, envía una notificación a los clientes conectados
         * indicando que se ha creado una nueva reserva.
         * </p>
         *
         * @param request datos necesarios para crear la reserva
         * @param session sesión HTTP del usuario
         * @return información de la reserva creada
         */
        public CourtBookingResponse create(CourtBookingRequest request, HttpSession session) {
                // Añadimos información relevante al MDC para que aparezca en todos los logs
                MDC.put("clienteId", String.valueOf(request.clienteId()));
                MDC.put("pistaId", String.valueOf(request.pistaId()));
                MDC.put("fecha", request.fecha().toString());

                try {
                        log.info("Solicitando creación de reserva de pista");

                        CourtBooking booking = courtBookingMapper.toEntity(request);

                        booking.setPista(courtRepository.getReferenceById(request.pistaId()));

                        booking.setHoraInicio(request.horaInicio());

                        booking.setHoraFin(request.horaFin());

                        booking.setFecha(request.fecha());

                        Long horas = ChronoUnit.HOURS.between(
                                        request.horaInicio(),
                                        request.horaFin());

                        booking.setCliente(
                                        userRepository.getReferenceById(request.clienteId()));

                        booking.setPrecioTotal(
                                        courtRepository.getReferenceById(request.pistaId())
                                                        .getPrecioPorHora() * horas);

                        courtBookingRepository.save(booking);

                        // Notificar a clientes conectados
                        Object payload = Map.of(
                                        "action",
                                        "CREATED",
                                        "courtId",
                                        booking.getPista().getId(),
                                        "fecha",
                                        booking.getFecha().toString());

                        messagingTemplate.convertAndSend(
                                        "/topic/court-updates",
                                        payload);

                        // registrar id de reserva en MDC y log de éxito a nivel de servicio
                        MDC.put("bookingId", String.valueOf(booking.getId()));
                        log.info("Reserva de pista creada");

                        return courtBookingMapper.toResponse(booking);
                } catch (RuntimeException ex) {
                        log.warn("Fallo al crear la reserva de pista", ex);
                        throw ex;
                } finally {
                        // limpiar sólo las claves que ha añadido este método
                        MDC.remove("bookingId");
                        MDC.remove("pistaId");
                        MDC.remove("clienteId");
                        MDC.remove("fecha");
                }
        }

        /**
         * Actualiza una reserva existente.
         * <p>
         * Valida que el rango horario sea correcto y que no existan
         * reservas solapadas para la pista y horario seleccionados.
         * También recalcula automáticamente el precio total.
         * </p>
         *
         * @param id      identificador de la reserva
         * @param request nuevos datos de la reserva
         * @return información actualizada de la reserva
         * @throws RuntimeException si la reserva no existe,
         *                          el horario es inválido o la pista no está disponible
         */
        public CourtBookingResponse update(Long id, CourtBookingRequest request) {
                MDC.put("bookingId", String.valueOf(id));
                MDC.put("pistaId", String.valueOf(request.pistaId()));
                MDC.put("clienteId", String.valueOf(request.clienteId()));

                try {
                        log.info("Solicitando actualización de reserva");

                        CourtBooking booking = courtBookingRepository.findById(id)
                                        .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

                        // Validar horas
                        if (request.horaInicio().isAfter(request.horaFin()) ||
                                        request.horaInicio().equals(request.horaFin())) {

                                throw new RuntimeException(
                                                "La hora de inicio debe ser menor que la de fin");
                        }

                        // Validar disponibilidad
                        List<CourtBooking> solapamientos = courtBookingRepository.findSolapamientos(
                                        request.pistaId(),
                                        request.fecha(),
                                        request.horaInicio(),
                                        request.horaFin()).stream().filter(b -> !b.getId().equals(id)).toList();

                        if (!solapamientos.isEmpty()) {

                                throw new RuntimeException(
                                                "La pista no está disponible");
                        }

                        // Actualizar datos
                        courtBookingMapper.updateEntityFromRequest(request, booking);

                        Court pista = courtRepository.findById(request.pistaId())
                                        .orElseThrow(() -> new RuntimeException("Pista no encontrada"));

                        booking.setPista(pista);

                        // Recalcular precio
                        long horas = request.horaInicio().until(
                                        request.horaFin(),
                                        java.time.temporal.ChronoUnit.HOURS);

                        booking.setPrecioTotal(
                                        horas * pista.getPrecioPorHora());

                        CourtBooking updated = courtBookingRepository.save(booking);

                        log.info("Reserva actualizada");

                        return courtBookingMapper.toResponse(updated);
                } catch (RuntimeException ex) {
                        log.warn("Fallo al actualizar la reserva", ex);
                        throw ex;
                } finally {
                        MDC.remove("bookingId");
                        MDC.remove("pistaId");
                        MDC.remove("clienteId");
                }
        }

        /**
         * Elimina una reserva existente.
         * <p>
         * Tras eliminar la reserva, se envía una notificación
         * a los clientes conectados mediante WebSocket.
         * </p>
         *
         * @param id identificador de la reserva
         * @throws RuntimeException si la reserva no existe
         */
        public void delete(Long id) {
                MDC.put("bookingId", String.valueOf(id));

                try {
                        log.info("Solicitando eliminación de reserva");

                        if (!courtBookingRepository.existsById(id)) {

                                throw new RuntimeException("Reserva no encontrada");
                        }

                        courtBookingRepository.deleteById(id);

                        Object payload = Map.of(
                                        "action",
                                        "DELETED",
                                        "bookingId",
                                        id);

                        messagingTemplate.convertAndSend(
                                        "/topic/court-updates",
                                        payload);

                        log.info("Reserva eliminada");
                } catch (RuntimeException ex) {
                        log.warn("Fallo al eliminar la reserva", ex);
                        throw ex;
                } finally {
                        MDC.remove("bookingId");
                }
        }

        public void validarReserva(Long idReserva, Long idRecepcionista) {
                MDC.put("bookingId", String.valueOf(idReserva));
                MDC.put("recepcionistaId", String.valueOf(idRecepcionista));

                try {
                        log.info("Solicitando validación de reserva");

                        if (idRecepcionista == null) {
                                throw new IllegalArgumentException("Usuario no autenticado");
                        }

                        User recepcionista = userRepository.findById(idRecepcionista)
                                        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

                        if (recepcionista.getRol() != Role.RECEPTIONIST) {
                                throw new IllegalArgumentException("Usuario no autorizado");
                        }

                        CourtBooking reserva = courtBookingRepository.findById(idReserva)
                                        .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

                        if (reserva.getEstado() != CourtBookingStatus.PENDIENTE) {
                                throw new IllegalArgumentException("La reserva no está en estado pendiente");
                        }

                        reserva.setEstado(CourtBookingStatus.CONFIRMADA);
                        courtBookingRepository.save(reserva);

                        log.info("Reserva validada");
                } catch (IllegalArgumentException ex) {
                        log.warn("Fallo en la validación de la reserva", ex);
                        throw ex;
                } finally {
                        MDC.remove("bookingId");
                        MDC.remove("recepcionistaId");
                }
        }

        /**
         * Obtiene todas las reservas asociadas a un cliente.
         *
         * @param clienteId identificador del cliente
         * @return lista de reservas del cliente
         */
        @Transactional(readOnly = true)
        public List<CourtBookingResponse> findByClienteId(Long clienteId) {
                MDC.put("clienteId", String.valueOf(clienteId));

                try {
                        log.info("Buscando reservas por cliente");
                        return courtBookingRepository.findByClienteId(clienteId)
                                        .stream()
                                        .map(courtBookingMapper::toResponse)
                                        .toList();
                } finally {
                        MDC.remove("clienteId");
                }
        }

        /*
         * // GET ALL
         * 
         * @Transactional(readOnly = true)
         * public List<CourtBookingResponse> findAll() {
         * return courtBookingRepository.findAll()
         * .stream()
         * .map(courtBookingMapper::toResponse)
         * .toList();
         * }
         * 
         * // GET BY ID
         * 
         * @Transactional(readOnly = true)
         * public CourtBookingResponse findById(Long id) {
         * CourtBooking booking = courtBookingRepository.findById(id)
         * .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
         * 
         * return courtBookingMapper.toResponse(booking);
         * }
         */

        /*
         * // FIND BY PISTA
         * 
         * @Transactional(readOnly = true)
         * public List<CourtBookingResponse> findByPistaId(Long pistaId) {
         * return courtBookingRepository.findByPistaId(pistaId)
         * .stream()
         * .map(courtBookingMapper::toResponse)
         * .toList();
         * 
         * // DISPONIBILIDAD
         * 
         * @Transactional(readOnly = true)
         * public boolean isDisponible(Long pistaId, LocalDate fecha, LocalTime
         * horaInicio, LocalTime horaFin) {
         * 
         * return courtBookingRepository.findSolapamientos(
         * pistaId,
         * fecha,
         * horaInicio,
         * horaFin
         * ).isEmpty();
         * }
         * 
         * 
         * }
         */

        public List<CourtBookingResponse> findAll() {
                try {
                        log.info("Obteniendo todas las reservas (servicio)");
                        return courtBookingRepository.findAll()
                                        .stream()
                                        .map(courtBookingMapper::toResponse)
                                        .toList();
                } catch (RuntimeException ex) {
                        log.warn("Fallo obteniendo todas las reservas", ex);
                        throw ex;
                }
        }

        public CourtBookingResponse findById(Long id) {
                MDC.put("bookingId", String.valueOf(id));

                try {
                        log.info("Buscando reserva por ID");

                        CourtBooking booking = courtBookingRepository.findById(id)
                                        .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

                        log.info("Reserva encontrada");

                        return new CourtBookingResponse(
                                        booking.getId(),
                                        booking.getCliente().getId(),
                                        booking.getCliente().getNombre(),
                                        booking.getPista().getId(),
                                        booking.getPista().getNombre(),
                                        booking.getFecha(),
                                        booking.getHoraInicio(),
                                        booking.getHoraFin(),
                                        booking.getEstado(),
                                        booking.getPrecioTotal(),
                                        booking.getCreadaEn());
                } catch (IllegalArgumentException ex) {
                        log.warn("Fallo buscando la reserva por ID", ex);
                        throw ex;
                } finally {
                        MDC.remove("bookingId");
                }
        }

        // con este metodo se cancela una reserva POR PARTE DEL ADMIN
        public void cancelBookingAdmin(Long id) {
                MDC.put("bookingId", String.valueOf(id));

                try {
                        log.info("Solicitando cancelación de reserva por admin");

                        CourtBooking booking = courtBookingRepository.findById(id)
                                        .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

                        booking.setEstado(com.example.deusto_hotel.model.CourtBookingStatus.CANCELADA);
                        courtBookingRepository.save(booking);
                        // notifica al usuario que ha sido cancelada mediante websocket
                        Object payload = java.util.Map.of(
                                        "action", "CANCELLED",
                                        "bookingId", id);
                        messagingTemplate.convertAndSend("/topic/court-updates", payload);

                        log.info("Reserva cancelada por admin");
                } catch (RuntimeException ex) {
                        log.warn("Fallo cancelando la reserva por admin", ex);
                        throw ex;
                } finally {
                        MDC.remove("bookingId");
                }
        }
}