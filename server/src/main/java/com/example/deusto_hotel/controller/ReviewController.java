package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.ReviewRequest;
import com.example.deusto_hotel.dto.ReviewResponse;
import com.example.deusto_hotel.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /*
    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAll() {
        // Implementar
        throw new UnsupportedOperationException();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getById(@PathVariable Long id) {
        // Implementar
        throw new UnsupportedOperationException();
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@RequestBody @Valid ReviewRequest request) {
        // Implementar
        throw new UnsupportedOperationException();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid ReviewRequest request) {
        // Implementar
        throw new UnsupportedOperationException();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // Implementar
        throw new UnsupportedOperationException();
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ReviewResponse>> getByClienteId(@PathVariable Long clienteId) {
        // Implementar
        throw new UnsupportedOperationException();
    }

    @GetMapping("/habitacion/{habitacionId}")
    public ResponseEntity<List<ReviewResponse>> getByHabitacionId(@PathVariable Long habitacionId) {
        // Implementar
        throw new UnsupportedOperationException();
    }
    */

}
