package com.example.deusto_hotel.repository;

import com.example.deusto_hotel.model.Room;
import com.example.deusto_hotel.model.RoomStatus;
import com.example.deusto_hotel.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByEstado(RoomStatus estado);

    List<Room> findByTipo(RoomType tipo);

    Optional<Room> findByNumero(String numero);

    boolean existsByNumero(String numero);
}
