package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.*;
import com.example.deusto_hotel.model.Court;
import com.example.deusto_hotel.model.CourtBookingStatus;
import com.example.deusto_hotel.model.CourtStatus;
import com.example.deusto_hotel.model.CourtType;
import com.example.deusto_hotel.repository.CourtBookingRepository;
import com.example.deusto_hotel.repository.CourtRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
@Slf4j
public class CourtService {

    /**
     * Repositorio de acceso a datos de pistas deportivas.
     */
    private final CourtRepository courtRepository;

    /**
     * Repositorio encargado de gestionar las reservas de pistas.
     */
    private final CourtBookingRepository courtBookingRepository;

	/**
	 * Obtiene todas las pistas deportivas del sistema.
	 * <p>
	 * Retorna una lista con todas las pistas registradas, sin aplicar
	 * ningún tipo de filtro. Cada pista incluye su identificador, nombre,
	 * tipo, precio por hora y estado actual.
	 * </p>
	 *
	 * @return lista completa de todas las pistas del sistema
	 */
	@Transactional(readOnly = true)
	public List<CourtResponse> findAll() {
		try {
			MDC.put("operationType", "find_all_courts");

			log.info("Obteniendo todas las pistas del sistema");

			List<CourtResponse> courts = courtRepository.findAll().stream()
					.map(c -> new CourtResponse(c.getId(), c.getNombre(), c.getTipo(), c.getPrecioPorHora(), c.getEstado()))
					.toList();

			log.info("Total de pistas obtenidas sin filtrar: {}", courts.size());

			return courts;
		} finally {
			MDC.clear();
		}
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
        try {
            MDC.put("operationType", "find_weekly_availability");
            MDC.put("year", String.valueOf(year));
            MDC.put("month", String.valueOf(month));
            MDC.put("courtType", type != null ? type.toString() : "ALL");

            log.info("Consultando disponibilidad semanal para {}-{}, tipo: {}", year, month, type);

            YearMonth yearMonth = YearMonth.of(year, month);
            LocalDate startOfMonth = yearMonth.atDay(1);
            LocalDate endOfMonth = yearMonth.atEndOfMonth();

            List<Court> courts;

            if (type != null) {
                courts = courtRepository.findByTipoAndEstado(type, CourtStatus.DISPONIBLE);
                log.info("Se encontraron {} pistas disponibles de tipo {}", courts.size(), type);
            } else {
                courts = courtRepository.findByEstado(CourtStatus.DISPONIBLE);
                log.info("Se encontraron {} pistas disponibles en total", courts.size());
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

            log.info("Se procesaron {} semanas de disponibilidad", weeks.size());

            return weeks;
        } finally {
            MDC.clear();
        }
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
        try {
            MDC.put("operationType", "find_available_by_type_and_week");
            MDC.put("courtType", tipo != null ? tipo : "ALL");
            MDC.put("week", semana != null ? String.valueOf(semana) : "CURRENT");

            log.info("Consultando disponibilidad por tipo: {}, semana: {}", tipo, semana);

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

            log.debug("Rango de fechas calculado: {} a {}", startDate, endDate);

            // Filtrar por tipo de pista si se proporciona
            List<Court> courts;

            if (tipo != null && !tipo.trim().isEmpty()) {

                try {

                    CourtType courtType = CourtType.valueOf(tipo.toUpperCase());

                    courts = courtRepository.findByTipoAndEstado(
                            courtType,
                            CourtStatus.DISPONIBLE);

                    log.info("Se encontraron {} pistas de tipo {}", courts.size(), courtType);

                } catch (Exception e) {

                    log.warn("Tipo de pista inválido: {}, retornando todas las pistas disponibles", tipo);
                    courts = courtRepository.findByEstado(CourtStatus.DISPONIBLE);
                }

            } else {

                courts = courtRepository.findByEstado(CourtStatus.DISPONIBLE);
                log.info("Se encontraron {} pistas disponibles en total", courts.size());
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
                log.info("Tipo: {}, reservas encontradas: {}", courtType, bookings.size());
            }

            log.info("Disponibilidad procesada: {} tipos de pista con reservas", result.size());

            return result;
        } finally {
            MDC.clear();
        }
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
        try {
            MDC.put("operationType", "find_available_by_date");
            MDC.put("fecha", fecha != null ? fecha : "INVALID");

            log.info("Consultando disponibilidad para fecha: {}", fecha);

            LocalDate targetDate;

            try {

                targetDate = LocalDate.parse(fecha);
                log.debug("Fecha parseada correctamente: {}", targetDate);

            } catch (Exception e) {

                log.warn("Formato de fecha inválido: {}", fecha);
                return List.of();
            }

            LocalDate today = LocalDate.now();

            // Si estamos en 2026, validar que la fecha sea válida
            if (today.getYear() == 2026) {

                LocalDate endOf2027 = LocalDate.of(2027, 12, 31);

                if (targetDate.isAfter(endOf2027)) {

                    log.warn("Fecha {} excede el límite permitido (2027-12-31)", targetDate);
                    return List.of();
                }
            }

            List<Court> courts = courtRepository.findByEstado(CourtStatus.DISPONIBLE);
            log.info("Se encontraron {} pistas disponibles para la fecha {}", courts.size(), targetDate);

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
                log.info("Tipo: {}, reservas para {}: {}", courtType, targetDate, bookings.size());
            }

            log.info("Disponibilidad para fecha {} procesada: {} tipos de pista", targetDate, result.size());

            return result;
        } finally {
            MDC.clear();
        }
    }

	/**
	 * Bloquea una pista deportiva para mantenimiento.
	 * <p>
	 * Cambia el estado de la pista a BLOQUEADA, lo que impide que se
	 * puedan realizar nuevas reservas en la misma hasta que sea desbloqueada.
	 * Esta operación es típicamente utilizada cuando se requiere
	 * mantenimiento o reparación de la pista.
	 * </p>
	 *
	 * @param id identificador de la pista a bloquear
	 * @return información actualizada de la pista bloqueada
	 * @throws IllegalArgumentException si la pista con el ID especificado no existe
	 */
	@Transactional
	public CourtResponse blockCourt(Long id) {
		try {
			MDC.put("operationType", "block_court");
			MDC.put("courtId", String.valueOf(id));

			log.info("Iniciando bloqueo de pista con ID: {}", id);

			Court court = courtRepository.findById(id)
					.orElseThrow(() -> {
						log.warn("Pista con ID {} no encontrada para bloqueo", id);
						return new IllegalArgumentException("Pista no encontrada con ID: " + id);
					});

			court.setEstado(CourtStatus.BLOQUEADA);
			courtRepository.save(court);

			log.info("Pista {} bloqueada exitosamente", id);

			return new CourtResponse(
					court.getId(),
					court.getNombre(),
					court.getTipo(),
					court.getPrecioPorHora(),
					court.getEstado());
		} finally {
			MDC.clear();
		}
	}

	/**
	 * Desbloquea una pista deportiva después del mantenimiento.
	 * <p>
	 * Cambia el estado de la pista de BLOQUEADA a DISPONIBLE, permitiendo
	 * que se puedan realizar nuevas reservas en la misma. Esta operación
	 * se realiza típicamente después de completar el mantenimiento o
	 * reparación de la pista.
	 * </p>
	 *
	 * @param id identificador de la pista a desbloquear
	 * @return información actualizada de la pista desbloqueada
	 * @throws IllegalArgumentException si la pista con el ID especificado no existe
	 */
	@Transactional
	public CourtResponse unblockCourt(Long id) {
		try {
			MDC.put("operationType", "unblock_court");
			MDC.put("courtId", String.valueOf(id));

			log.info("Iniciando desbloqueo de pista con ID: {}", id);

			Court court = courtRepository.findById(id)
					.orElseThrow(() -> {
						log.warn("Pista con ID {} no encontrada para desbloqueo", id);
						return new IllegalArgumentException("Pista no encontrada con ID: " + id);
					});

			court.setEstado(CourtStatus.DISPONIBLE);
			courtRepository.save(court);

			log.info("Pista {} desbloqueada exitosamente", id);

			return new CourtResponse(
					court.getId(),
					court.getNombre(),
					court.getTipo(),
					court.getPrecioPorHora(),
					court.getEstado());
		} finally {
			MDC.clear();
		}
	}

}