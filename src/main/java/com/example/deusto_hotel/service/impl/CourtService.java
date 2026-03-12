package com.example.deusto_hotel.service.impl;

import com.example.deusto_hotel.dto.CourtRequest;
import com.example.deusto_hotel.dto.CourtResponse;
import com.example.deusto_hotel.repository.CourtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourtService {

    private final CourtRepository courtRepository;

    
    @Transactional(readOnly = true)
    public List<CourtResponse> findAll() {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    @Transactional(readOnly = true)
    public CourtResponse findById(Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    public CourtResponse create(CourtRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    public CourtResponse update(Long id, CourtRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    public void delete(Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }
}
