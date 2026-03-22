package com.example.deusto_hotel.repository;

import com.example.deusto_hotel.model.CourtBooking;
import com.example.deusto_hotel.model.CourtBookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CourtBookingRepository extends JpaRepository<CourtBooking, Long> {

    // Básicos
    List<CourtBooking> findByClienteId(Long clienteId);

    List<CourtBooking> findByPistaId(Long pistaId);

    List<CourtBooking> findByEstado(CourtBookingStatus estado);

    List<CourtBooking> findByClienteIdAndEstado(Long clienteId,
                                                CourtBookingStatus estado);

    // Buscar por fecha
    List<CourtBooking> findByFecha(LocalDate fecha);

    List<CourtBooking> findByPistaIdAndFecha(Long pistaId, LocalDate fecha);

    // Ordenaciones
    List<CourtBooking> findByPistaIdOrderByFechaAscHoraInicioAsc(Long pistaId);

    List<CourtBooking> findByClienteIdOrderByFechaDesc(Long clienteId);

    // SOLAPAMIENTOS
    @Query("SELECT c FROM CourtBooking c WHERE c.pista.id = :pistaId " +
            "AND c.fecha = :fecha " +
            "AND c.estado != 'CANCELADA' " +
            "AND c.horaInicio < :horaFin AND c.horaFin > :horaInicio")
    List<CourtBooking> findSolapamientos(@Param("pistaId") Long pistaId,
                                         @Param("fecha") LocalDate fecha,
                                         @Param("horaInicio") LocalTime horaInicio,
                                         @Param("horaFin") LocalTime horaFin);

    // Reservas activas
    @Query("SELECT c FROM CourtBooking c WHERE c.estado != 'CANCELADA'")
    List<CourtBooking> findActivas();

    // Reservas futuras
    @Query("SELECT c FROM CourtBooking c WHERE c.fecha >= :hoy")
    List<CourtBooking> findFuturas(@Param("hoy") LocalDate hoy);

    // Reservas de una pista en un día ordenadas
    @Query("SELECT c FROM CourtBooking c WHERE c.pista.id = :pistaId " +
            "AND c.fecha = :fecha " +
            "ORDER BY c.horaInicio ASC")
    List<CourtBooking> findByPistaAndFechaOrdenado(@Param("pistaId") Long pistaId,
                                                   @Param("fecha") LocalDate fecha);
}