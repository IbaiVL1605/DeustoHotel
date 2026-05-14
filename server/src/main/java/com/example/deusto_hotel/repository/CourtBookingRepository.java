package com.example.deusto_hotel.repository;

import com.example.deusto_hotel.model.CourtBooking;
import com.example.deusto_hotel.model.CourtBookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Repositorio encargado de la gestión de reservas de pistas.
 * <p>
 * Proporciona métodos de acceso a datos relacionados con
 * reservas, disponibilidad y búsquedas filtradas.
 * </p>
 */
public interface CourtBookingRepository extends JpaRepository<CourtBooking, Long> {

    /**
     * Obtiene las reservas asociadas a un cliente.
     *
     * @param clienteId identificador del cliente
     * @return lista de reservas del cliente
     */
    List<CourtBooking> findByClienteId(Long clienteId);

    /**
     * Obtiene las reservas asociadas a una pista.
     *
     * @param pistaId identificador de la pista
     * @return lista de reservas de la pista
     */
    List<CourtBooking> findByPistaId(Long pistaId);

    /**
     * Obtiene las reservas filtradas por estado.
     *
     * @param estado estado de la reserva
     * @return lista de reservas con el estado indicado
     */
    List<CourtBooking> findByEstado(CourtBookingStatus estado);

    /**
     * Obtiene las reservas de un cliente filtradas por estado.
     *
     * @param clienteId identificador del cliente
     * @param estado estado de la reserva
     * @return lista de reservas filtradas
     */
    List<CourtBooking> findByClienteIdAndEstado(Long clienteId,
                                                CourtBookingStatus estado);

    /**
     * Obtiene las reservas de una fecha concreta.
     *
     * @param fecha fecha de la reserva
     * @return lista de reservas de la fecha indicada
     */
    List<CourtBooking> findByFecha(LocalDate fecha);

    /**
     * Obtiene las reservas de una pista para una fecha concreta.
     *
     * @param pistaId identificador de la pista
     * @param fecha fecha de la reserva
     * @return lista de reservas encontradas
     */
    List<CourtBooking> findByPistaIdAndFecha(Long pistaId, LocalDate fecha);

    /**
     * Obtiene las reservas de una pista ordenadas por fecha
     * y hora de inicio ascendente.
     *
     * @param pistaId identificador de la pista
     * @return lista de reservas ordenadas
     */
    List<CourtBooking> findByPistaIdOrderByFechaAscHoraInicioAsc(Long pistaId);

    /**
     * Obtiene las reservas de un cliente ordenadas por fecha descendente.
     *
     * @param clienteId identificador del cliente
     * @return lista de reservas ordenadas
     */
    List<CourtBooking> findByClienteIdOrderByFechaDesc(Long clienteId);

    /**
     * Obtiene las reservas que se solapan con un intervalo horario
     * determinado para una pista y fecha concretas.
     * <p>
     * Se excluyen automáticamente las reservas canceladas.
     * </p>
     *
     * @param pistaId identificador de la pista
     * @param fecha fecha de la reserva
     * @param horaInicio hora inicial del intervalo
     * @param horaFin hora final del intervalo
     * @return lista de reservas solapadas
     */
    @Query("SELECT c FROM CourtBooking c WHERE c.pista.id = :pistaId " +
            "AND c.fecha = :fecha " +
            "AND c.estado != 'CANCELADA' " +
            "AND c.horaInicio < :horaFin AND c.horaFin > :horaInicio")
    List<CourtBooking> findSolapamientos(
            @Param("pistaId") Long pistaId,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin
    );

    /**
     * Obtiene todas las reservas activas del sistema.
     * <p>
     * Se consideran activas aquellas cuyo estado
     * es distinto de CANCELADA.
     * </p>
     *
     * @return lista de reservas activas
     */
    @Query("SELECT c FROM CourtBooking c WHERE c.estado != 'CANCELADA'")
    List<CourtBooking> findActivas();

    /**
     * Obtiene todas las reservas futuras a partir de una fecha.
     *
     * @param hoy fecha de referencia
     * @return lista de reservas futuras
     */
    @Query("SELECT c FROM CourtBooking c WHERE c.fecha >= :hoy")
    List<CourtBooking> findFuturas(@Param("hoy") LocalDate hoy);

    /**
     * Obtiene las reservas de una pista para una fecha concreta,
     * ordenadas por hora de inicio ascendente.
     *
     * @param pistaId identificador de la pista
     * @param fecha fecha de la reserva
     * @return lista de reservas ordenadas
     */
    @Query("SELECT c FROM CourtBooking c WHERE c.pista.id = :pistaId " +
            "AND c.fecha = :fecha " +
            "ORDER BY c.horaInicio ASC")
    List<CourtBooking> findByPistaAndFechaOrdenado(
            @Param("pistaId") Long pistaId,
            @Param("fecha") LocalDate fecha
    );
}