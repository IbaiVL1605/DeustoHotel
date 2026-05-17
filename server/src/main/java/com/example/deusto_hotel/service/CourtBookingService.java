package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.CourtBookingRequest;
import com.example.deusto_hotel.dto.CourtBookingResponse;
import com.example.deusto_hotel.mapper.CourtBookingMapper;
import com.example.deusto_hotel.model.Court;
import com.example.deusto_hotel.model.CourtBooking;
import com.example.deusto_hotel.repository.CourtBookingRepository;
import com.example.deusto_hotel.repository.CourtRepository;
import com.example.deusto_hotel.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;

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

                return courtBookingMapper.toResponse(booking);
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

                return courtBookingMapper.toResponse(updated);
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
        }

        /**
         * Obtiene todas las reservas asociadas a un cliente.
         *
         * @param clienteId identificador del cliente
         * @return lista de reservas del cliente
         */
        @Transactional(readOnly = true)
        public List<CourtBookingResponse> findByClienteId(Long clienteId) {

                return courtBookingRepository.findByClienteId(clienteId)
                                .stream()
                                .map(courtBookingMapper::toResponse)
                                .toList();
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
                return courtBookingRepository.findAll()
                                .stream()
                                .map(courtBookingMapper::toResponse)
                                .toList();
        }

        public CourtBookingResponse findById(Long id) {

                CourtBooking booking = courtBookingRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

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
        }

        // con este metodo se cancela una reserva POR PARTE DEL ADMIN
        public void cancelBookingAdmin(Long id) {
                CourtBooking booking = courtBookingRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

                booking.setEstado(com.example.deusto_hotel.model.CourtBookingStatus.CANCELADA);
                courtBookingRepository.save(booking);
                // notifica al usuario que ha sido cancelada mediante websocket
                Object payload = java.util.Map.of(
                                "action", "CANCELLED",
                                "bookingId", id);
                messagingTemplate.convertAndSend("/topic/court-updates", payload);
        }
}