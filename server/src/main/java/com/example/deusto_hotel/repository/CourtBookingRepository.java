package com.example.deusto_hotel.repository;

import com.example.deusto_hotel.model.CourtBooking;
import com.example.deusto_hotel.model.CourtBookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CourtBookingRepository extends JpaRepository<CourtBooking, Long> {

    List<CourtBooking> findByClienteId(Long clienteId);

    List<CourtBooking> findByPistaId(Long pistaId);

    List<CourtBooking> findByEstado(CourtBookingStatus estado);

    @Query("SELECT c FROM CourtBooking c WHERE c.pista.id = :pistaId " +
           "AND c.fecha = :fecha " +
           "AND c.estado != 'CANCELADA' " +
           "AND c.horaInicio < :horaFin AND c.horaFin > :horaInicio")
    List<CourtBooking> findSolapamientos(@Param("pistaId") Long pistaId,
                                         @Param("fecha") LocalDate fecha,
                                         @Param("horaInicio") LocalTime horaInicio,
                                         @Param("horaFin") LocalTime horaFin);

    List<CourtBooking> findByClienteIdAndEstado(Long clienteId,
                                                CourtBookingStatus estado);
}
