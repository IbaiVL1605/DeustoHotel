package com.example.deusto_hotel.repository;

import com.example.deusto_hotel.model.Court;
import com.example.deusto_hotel.model.CourtStatus;
import com.example.deusto_hotel.model.CourtType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {

    List<Court> findByEstado(CourtStatus estado);

    List<Court> findByTipo(CourtType tipo);

    boolean existsByNombre(String nombre);
}
