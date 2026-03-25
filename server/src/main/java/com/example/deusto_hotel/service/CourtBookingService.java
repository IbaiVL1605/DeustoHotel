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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourtBookingService {

    private final CourtBookingRepository courtBookingRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;
    private final CourtBookingMapper courtBookingMapper;

    // 🔹 GET ALL
    @Transactional(readOnly = true)
    public List<CourtBookingResponse> findAll() {
        return courtBookingRepository.findAll()
                .stream()
                .map(courtBookingMapper::toResponse)
                .toList();
    }

    // 🔹 GET BY ID
    @Transactional(readOnly = true)
    public CourtBookingResponse findById(Long id) {
        CourtBooking booking = courtBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        return courtBookingMapper.toResponse(booking);
    }

    // 🔹 CREATE
    public CourtBookingResponse create(CourtBookingRequest request, HttpSession session) {

        CourtBooking booking = courtBookingMapper.toEntity(request);

        booking.setPista(courtRepository.getReferenceById(request.pistaId()));

        booking.setHoraInicio(request.horaInicio());

        booking.setHoraFin(request.horaFin());

        booking.setFecha(request.fecha());

        Long horas = ChronoUnit.HOURS.between(request.horaFin(), request.horaInicio());

        booking.setCliente(userRepository.getReferenceById(Long.parseLong(session.getId())));

        booking.setPrecioTotal(courtRepository.getReferenceById(request.pistaId()).getPrecioPorHora() * horas);

        courtBookingRepository.save(booking);

        return courtBookingMapper.toResponse(booking);
    }

    // 🔹 UPDATE
    public CourtBookingResponse update(Long id, CourtBookingRequest request) {

        CourtBooking booking = courtBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // Validar horas
        if (request.horaInicio().isAfter(request.horaFin()) ||
                request.horaInicio().equals(request.horaFin())) {
            throw new RuntimeException("La hora de inicio debe ser menor que la de fin");
        }

        // Validar disponibilidad (excluyendo la propia reserva)
        List<CourtBooking> solapamientos = courtBookingRepository.findSolapamientos(
                request.pistaId(),
                request.fecha(),
                request.horaInicio(),
                request.horaFin()
        ).stream().filter(b -> !b.getId().equals(id)).toList();

        if (!solapamientos.isEmpty()) {
            throw new RuntimeException("La pista no está disponible");
        }

        // Actualizar datos
        courtBookingMapper.updateEntityFromRequest(request, booking);

        Court pista = courtRepository.findById(request.pistaId())
                .orElseThrow(() -> new RuntimeException("Pista no encontrada"));

        booking.setPista(pista);

        // Recalcular precio
        long horas = request.horaInicio().until(request.horaFin(), java.time.temporal.ChronoUnit.HOURS);
        booking.setPrecioTotal(horas * pista.getPrecioPorHora());

        CourtBooking updated = courtBookingRepository.save(booking);

        return courtBookingMapper.toResponse(updated);
    }

    // 🔹 DELETE
    public void delete(Long id) {
        if (!courtBookingRepository.existsById(id)) {
            throw new RuntimeException("Reserva no encontrada");
        }
        courtBookingRepository.deleteById(id);
    }

    // 🔹 FIND BY CLIENTE
    @Transactional(readOnly = true)
    public List<CourtBookingResponse> findByClienteId(Long clienteId) {
        return courtBookingRepository.findByClienteId(clienteId)
                .stream()
                .map(courtBookingMapper::toResponse)
                .toList();
    }

    // 🔹 FIND BY PISTA
    @Transactional(readOnly = true)
    public List<CourtBookingResponse> findByPistaId(Long pistaId) {
        return courtBookingRepository.findByPistaId(pistaId)
                .stream()
                .map(courtBookingMapper::toResponse)
                .toList();
    }

    // 🔹 DISPONIBILIDAD
    @Transactional(readOnly = true)
    public boolean isDisponible(Long pistaId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {

        return courtBookingRepository.findSolapamientos(
                pistaId,
                fecha,
                horaInicio,
                horaFin
        ).isEmpty();
    }
}