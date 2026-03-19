package com.example.deusto_hotel.repository;

import com.example.deusto_hotel.model.Room;
import com.example.deusto_hotel.model.RoomStatus;
import com.example.deusto_hotel.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByEstado(RoomStatus estado);

    List<Room> findByTipo(RoomType tipo);

    Optional<Room> findByNumero(String numero);

    @Query("SELECT r FROM Room r WHERE r.estado = 'DISPONIBLE' AND " +
           "r.id NOT IN (SELECT rb.habitacion.id FROM RoomBooking rb " +
           "WHERE rb.estado != 'CANCELADA' AND rb.checkIn < :fechaSalida AND rb.checkOut > :fechaEntrada)")
    List<Room> findRoomDisponibles(LocalDate fechaEntrada, LocalDate fechaSalida);

    boolean existsByNumero(String numero);
}
