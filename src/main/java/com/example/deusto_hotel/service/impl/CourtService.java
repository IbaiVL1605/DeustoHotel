package com.example.deusto_hotel.service.impl;

import com.example.deusto_hotel.dto.CourtRequest;
import com.example.deusto_hotel.dto.CourtResponse;
import com.example.deusto_hotel.repository.CourtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.deusto_hotel.model.Court;
import com.example.deusto_hotel.model.CourtBookingStatus;
import com.example.deusto_hotel.model.CourtStatus;
import com.example.deusto_hotel.model.CourtType;

@Service
@RequiredArgsConstructor
@Transactional
public class CourtService {

    private final CourtRepository courtRepository;

    @Transactional(readOnly = true)
    public List<CourtResponse> findAll() {
        throw new UnsupportedOperationException();
    }

    @Transactional(readOnly = true)
    public CourtResponse findById(Long id) {
        throw new UnsupportedOperationException();
    }

    public CourtResponse create(CourtRequest request) {
        throw new UnsupportedOperationException();
    }

    public CourtResponse update(Long id, CourtRequest request) {
        throw new UnsupportedOperationException();
    }

    public void delete(Long id) {
        throw new UnsupportedOperationException();
    }

    @Transactional(readOnly = true)
    public List<CourtResponse> findAvailableCourts(String tipo, String fecha, String horaInicio, String horaFin) {
        CourtType courtType = null;
        if (tipo != null) {
            try {
                courtType = CourtType.valueOf(tipo.toUpperCase());
            } catch (Exception ignored) {}
        }
        LocalDate localDate = null;
        LocalTime start = null;
        LocalTime end = null;
        if (fecha != null && horaInicio != null && horaFin != null) {
            try {
                localDate = LocalDate.parse(fecha);
                start = LocalTime.parse(horaInicio);
                end = LocalTime.parse(horaFin);
            } catch (Exception ignored) {}
        }

        final CourtType finalCourtType = courtType;
        final LocalDate finalLocalDate = localDate;
        final LocalTime finalStart = start;
        final LocalTime finalEnd = end;

        List<Court> courts;
        if (finalCourtType != null) {
            courts = courtRepository.findByTipoAndEstado(finalCourtType, CourtStatus.DISPONIBLE);
        } else {
            courts = courtRepository.findByEstado(CourtStatus.DISPONIBLE);
        }

        if (finalLocalDate != null && finalStart != null && finalEnd != null) {
            courts = courts.stream().filter(court ->
                court.getCourtBookings().stream().noneMatch(booking ->
                    booking.getFecha().equals(finalLocalDate)
                    && booking.getEstado() != CourtBookingStatus.CANCELADA
                    && booking.getHoraInicio().isBefore(finalEnd)
                    && booking.getHoraFin().isAfter(finalStart)
                )
            ).toList();
        }

        return courts.stream().map(c -> new CourtResponse(
                c.getId(),
                c.getNombre(),
                c.getTipo(),
                c.getPrecioPorHora(),
                c.getEstado()
        )).toList();
    }
}
