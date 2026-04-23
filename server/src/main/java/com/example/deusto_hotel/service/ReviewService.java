package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.ReviewRequest;
import com.example.deusto_hotel.dto.ReviewResponse;
import com.example.deusto_hotel.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;

    
    /*
    @Transactional(readOnly = true)
    public List<ReviewResponse> findAll() {
        // Implementar
        throw new UnsupportedOperationException();
    }


    @Transactional(readOnly = true)
    public ReviewResponse findById(Long id) {
        // Implementar
        throw new UnsupportedOperationException();
    }


    public ReviewResponse create(ReviewRequest request) {
        // Implementar
        throw new UnsupportedOperationException();
    }


    public ReviewResponse update(Long id, ReviewRequest request) {
        // Implementar
        throw new UnsupportedOperationException();
    }


    public void delete(Long id) {
        // Implementar
        throw new UnsupportedOperationException();
    }


    @Transactional(readOnly = true)
    public List<ReviewResponse> findByClienteId(Long clienteId) {
        // Implementar
        throw new UnsupportedOperationException();
    }


    @Transactional(readOnly = true)
    public List<ReviewResponse> findByHabitacionId(Long habitacionId) {
        // Implementar
        throw new UnsupportedOperationException();
    }
     */
}
