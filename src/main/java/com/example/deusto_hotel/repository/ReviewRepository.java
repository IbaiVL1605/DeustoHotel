package com.example.deusto_hotel.repository;

import com.example.deusto_hotel.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByClienteId(Long clienteId);

    List<Review> findByHabitacionId(Long habitacionId);

    boolean existsByClienteIdAndHabitacionId(Long clienteId, Long habitacionId);
}
