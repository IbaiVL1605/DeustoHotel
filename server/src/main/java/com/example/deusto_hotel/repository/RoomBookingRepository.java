package com.example.deusto_hotel.repository;

import com.example.deusto_hotel.model.RoomBooking;
import com.example.deusto_hotel.model.RoomBookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomBookingRepository extends JpaRepository<RoomBooking, Long> {

    List<RoomBooking> findByClienteId(Long clienteId);

    List<RoomBooking> findByHabitacionId(Long habitacionId);

    List<RoomBooking> findByEstado(RoomBookingStatus estado);


    @Query("SELECT r FROM RoomBooking r WHERE r.habitacion.id = :habitacionId " +
           "AND r.estado != 'CANCELADA' " +
           "AND r.checkIn < :checkOut AND r.checkOut > :checkIn")
    List<RoomBooking> findSolapamientos(@Param("habitacionId") Long habitacionId,
                                        @Param("checkIn") LocalDate checkIn,
                                        @Param("checkOut") LocalDate checkOut);



    List<RoomBooking> findByClienteIdAndEstado(Long clienteId,
                                               RoomBookingStatus estado);
}
