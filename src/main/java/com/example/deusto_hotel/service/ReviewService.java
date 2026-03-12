package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.ReviewRequest;
import com.example.deusto_hotel.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {

    List<ReviewResponse> findAll();

    ReviewResponse findById(Long id);

    ReviewResponse create(ReviewRequest request);

    ReviewResponse update(Long id, ReviewRequest request);

    void delete(Long id);

    List<ReviewResponse> findByClienteId(Long clienteId);

    List<ReviewResponse> findByHabitacionId(Long habitacionId);
}
