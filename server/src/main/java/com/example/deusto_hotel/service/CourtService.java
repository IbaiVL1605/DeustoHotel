package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.*;
import com.example.deusto_hotel.model.Court;
import com.example.deusto_hotel.model.CourtBookingStatus;
import com.example.deusto_hotel.model.CourtStatus;
import com.example.deusto_hotel.model.CourtType;
import com.example.deusto_hotel.repository.CourtBookingRepository;
import com.example.deusto_hotel.repository.CourtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Servicio encargado de la gestión y consulta de disponibilidad
 * de pistas deportivas.
 * <p>
 * Permite consultar disponibilidad semanal, franjas horarias libres
 * y reservas agrupadas por tipo de pista y fechas.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CourtService {

    /**
     * Repositorio de acceso a datos de pistas deportivas.
     */
    private final CourtRepository courtRepository;

    /**
     * Repositorio encargado de gestionar las reservas de pistas.
     */
    private final CourtBookingRepository courtBookingRepository;

    @Transactional(readOnly = true)
    public List<CourtResponse> findAll() {
        return courtRepository.findAll().stream()
                .map(c -> new CourtResponse(c.getId(), c.getNombre(), c.getTipo(), c.getPrecioPorHora(), c.getEstado()))
                .toList();
    }
    /*
     * @Transactional(readOnly = true)
     * public CourtResponse findById(Long id) {
     * throw new UnsupportedOperationException();
     * }
     * 
     * 
     * public CourtResponse create(CourtRequest request) {
     * throw new UnsupportedOperationException();
     * }
     * 
     * public CourtResponse update(Long id, CourtRequest request) {
     * throw new UnsupportedOperationException();
     * }
     * 
     * public void delete(Long id) {
     * throw new UnsupportedOperationException();
     * }
     * 
     * 
     * @Transactional(readOnly = true)
     * public List<CourtResponse> findAvailableCourts(String tipo, String fecha,
     * String horaInicio, String horaFin) {
     * ...
     * }
     */

    /**
     * Obtiene la disponibilidad semanal de pistas para un mes concreto.
     * <p>
     * Genera una estructura organizada por semanas y días, incluyendo
     * las franjas horarias disponibles para cada fecha.
     * </p>
     *
     * @param year  año a consultar
     * @param month mes a consultar
     * @param type  tipo de pista a filtrar, o null para todas
     * @return lista de semanas con disponibilidad diaria
     */
    @Transactional(readOnly = true)
    public List<WeekAvailability> findWeeklyAvailability(int year, int month, CourtType type) {

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        List<Court> courts;

        if (type != null) {
            courts = courtRepository.findByTipoAndEstado(type, CourtStatus.DISPONIBLE);
        } else {
            courts = courtRepository.findByEstado(CourtStatus.DISPONIBLE);
        }

        List<WeekAvailability> weeks = new ArrayList<>();
        LocalDate current = startOfMonth;

        while (!current.isAfter(endOfMonth)) {

            int weekNumber = current.get(WeekFields.of(Locale.getDefault()).weekOfYear());

            List<DayAvailability> days = new ArrayList<>();

            for (int i = 0; i < 7 && !current.isAfter(endOfMonth); i++) {

                List<AvailableSlot> slots = getAvailableSlotsForDay(current, courts);

                days.add(new DayAvailability(current, slots));

                current = current.plusDays(1);
            }

            weeks.add(new WeekAvailability(weekNumber, days));
        }

        return weeks;
    }

    /**
     * Obtiene las franjas horarias disponibles para un día concreto.
     * <p>
     * Comprueba qué pistas no tienen reservas solapadas en cada
     * intervalo horario de una hora.
     * </p>
     *
     * @param date   fecha a consultar
     * @param courts lista de pistas disponibles
     * @return lista de franjas horarias disponibles
     */
    private List<AvailableSlot> getAvailableSlotsForDay(LocalDate date, List<Court> courts) {

        List<AvailableSlot> availableSlots = new ArrayList<>();

        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(22, 0);

        for (LocalTime current = startTime; current.isBefore(endTime); current = current.plusHours(1)) {

            final LocalTime slotStart = current;
            final LocalTime slotEnd = current.plusHours(1);

            List<Court> availableCourts = courts.stream().filter(court -> courtBookingRepository.findSolapamientos(
                    court.getId(),
                    date,
                    slotStart,
                    slotEnd).isEmpty()).toList();

            if (!availableCourts.isEmpty()) {

                List<CourtResponse> courtResponses = availableCourts.stream().map(c -> new CourtResponse(
                        c.getId(),
                        c.getNombre(),
                        c.getTipo(),
                        c.getPrecioPorHora(),
                        c.getEstado())).toList();

                availableSlots.add(
                        new AvailableSlot(slotStart, slotEnd, courtResponses));
            }
        }

        return availableSlots;
    }

    /*
     * @Transactional(readOnly = true)
     * public List<CourtDayAvailability> findCourtDayAvailability(String tipo,
     * String fecha) {
     * ...
     * }
     * 
     * @Transactional(readOnly = true)
     * public List<CourtDayAvailability> findCourtDayAvailabilityWithRange(String
     * tipo, String fecha, String horaInicio, String horaFin) {
     * ...
     * }
     */

    /**
     * Obtiene las reservas agrupadas por tipo de pista y semana.
     * <p>
     * Permite consultar las reservas confirmadas y pendientes
     * dentro de un rango semanal calculado automáticamente.
     * </p>
     *
     * @param tipo   tipo de pista a consultar
     * @param semana número de semana relativa:
     *               1 para la semana actual,
     *               2 para la siguiente
     * @return lista de disponibilidad agrupada por tipo de pista
     */
    @Transactional(readOnly = true)
    public List<CourtAvailabilityDTO> findAvailableByTypeAndWeek(String tipo, Integer semana) {

        LocalDate today = LocalDate.now();

        LocalDate startDate;
        LocalDate endDate;

        // Calcular rango de fechas basado en la semana del mes
        if (semana != null && (semana == 1 || semana == 2)) {

            startDate = today.plusDays((semana - 1) * 7);
            endDate = startDate.plusDays(6);

        } else {

            startDate = today;
            endDate = today.plusDays(6);
        }

        // Si estamos en 2026, solo mostrar hasta fin de año + siguiente año
        if (today.getYear() == 2026) {

            LocalDate endOfYear2026 = LocalDate.of(2026, 12, 31);
            LocalDate endOf2027 = LocalDate.of(2027, 12, 31);

            endDate = endDate.isAfter(endOf2027)
                    ? endOf2027
                    : endDate;
        }

        // Filtrar por tipo de pista si se proporciona
        List<Court> courts;

        if (tipo != null && !tipo.trim().isEmpty()) {

            try {

                CourtType courtType = CourtType.valueOf(tipo.toUpperCase());

                courts = courtRepository.findByTipoAndEstado(
                        courtType,
                        CourtStatus.DISPONIBLE);

            } catch (Exception e) {

                courts = courtRepository.findByEstado(CourtStatus.DISPONIBLE);
            }

        } else {

            courts = courtRepository.findByEstado(CourtStatus.DISPONIBLE);
        }

        // Agrupar reservas por tipo de pista
        List<CourtAvailabilityDTO> result = new ArrayList<>();

        for (CourtType courtType : CourtType.values()) {

            List<Court> courtsByType = courts.stream()
                    .filter(c -> c.getTipo() == courtType)
                    .toList();

            if (courtsByType.isEmpty())
                continue;

            // Obtener reservas confirmadas y pendientes
            List<CourtBookingResponse> bookings = new ArrayList<>();

            final LocalDate finalStartDate = startDate;
            final LocalDate finalEndDate = endDate;

            for (Court court : courtsByType) {

                List<CourtBookingResponse> courtBookings = court.getCourtBookings().stream()
                        .filter(booking -> booking.getFecha().isAfter(finalStartDate.minusDays(1)) &&
                                booking.getFecha().isBefore(finalEndDate.plusDays(1)) &&
                                booking.getEstado() != CourtBookingStatus.CANCELADA)
                        .map(booking -> new CourtBookingResponse(
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
                                booking.getCreadaEn()))
                        .toList();

                bookings.addAll(courtBookings);
            }

            result.add(new CourtAvailabilityDTO(courtType, bookings));
        }

        return result;
    }

    /*
     * @Transactional(readOnly = true)
     * public List<CourtAvailabilityDTO> findAvailableByType(String tipo) {
     * return findAvailableByTypeAndWeek(tipo, null);
     * }
     */

    /**
     * Obtiene las reservas disponibles para una fecha concreta.
     * <p>
     * Las reservas se agrupan por tipo de pista y se excluyen
     * aquellas que estén canceladas.
     * </p>
     *
     * @param fecha fecha a consultar en formato ISO yyyy-MM-dd
     * @return lista de disponibilidad agrupada por tipo de pista
     */
    @Transactional(readOnly = true)
    public List<CourtAvailabilityDTO> findAvailableByDate(String fecha) {

        LocalDate targetDate;

        try {

            targetDate = LocalDate.parse(fecha);

        } catch (Exception e) {

            return List.of();
        }

        LocalDate today = LocalDate.now();

        // Si estamos en 2026, validar que la fecha sea válida
        if (today.getYear() == 2026) {

            LocalDate endOf2027 = LocalDate.of(2027, 12, 31);

            if (targetDate.isAfter(endOf2027)) {

                return List.of();
            }
        }

        List<Court> courts = courtRepository.findByEstado(CourtStatus.DISPONIBLE);

        // Agrupar reservas por tipo de pista
        List<CourtAvailabilityDTO> result = new ArrayList<>();

        for (CourtType courtType : CourtType.values()) {

            List<Court> courtsByType = courts.stream()
                    .filter(c -> c.getTipo() == courtType)
                    .toList();

            if (courtsByType.isEmpty())
                continue;

            // Obtener reservas para esa fecha específica
            List<CourtBookingResponse> bookings = new ArrayList<>();

            for (Court court : courtsByType) {

                List<CourtBookingResponse> courtBookings = court.getCourtBookings().stream()
                        .filter(booking -> booking.getFecha().equals(targetDate) &&
                                booking.getEstado() != CourtBookingStatus.CANCELADA)
                        .map(booking -> new CourtBookingResponse(
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
                                booking.getCreadaEn()))
                        .toList();

                bookings.addAll(courtBookings);
            }

            result.add(new CourtAvailabilityDTO(courtType, bookings));
        }

        return result;
    }

    // para bloquear pistas en mantenimiento
    @Transactional
    public CourtResponse blockCourt(Long id) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pista no encontrada con ID: " + id));

        court.setEstado(CourtStatus.BLOQUEADA);
        courtRepository.save(court);

        return new CourtResponse(
                court.getId(),
                court.getNombre(),
                court.getTipo(),
                court.getPrecioPorHora(),
                court.getEstado());
    }

    // para desbloquear pistas al terminar el mantenimiento
    @Transactional
    public CourtResponse unblockCourt(Long id) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pista no encontrada con ID: " + id));

        court.setEstado(CourtStatus.DISPONIBLE);
        courtRepository.save(court);

        return new CourtResponse(
                court.getId(),
                court.getNombre(),
                court.getTipo(),
                court.getPrecioPorHora(),
                court.getEstado());
    }

}