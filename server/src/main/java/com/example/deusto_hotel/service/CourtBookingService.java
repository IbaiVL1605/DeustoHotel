package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.CourtBookingRequest;
import com.example.deusto_hotel.dto.CourtBookingResponse;
import com.example.deusto_hotel.repository.CourtBookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourtBookingService {

    private final CourtBookingRepository courtBookingRepository;

    
    @Transactional(readOnly = true)
    public List<CourtBookingResponse> findAll() {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    @Transactional(readOnly = true)
    public CourtBookingResponse findById(Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    public CourtBookingResponse create(CourtBookingRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    public CourtBookingResponse update(Long id, CourtBookingRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    public void delete(Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    @Transactional(readOnly = true)
    public List<CourtBookingResponse> findByClienteId(Long clienteId) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    @Transactional(readOnly = true)
    public List<CourtBookingResponse> findByPistaId(Long pistaId) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }
}
