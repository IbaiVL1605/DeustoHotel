package com.example.deusto_hotel.service.impl;

import com.example.deusto_hotel.dto.CourtRequest;
import com.example.deusto_hotel.dto.CourtResponse;
import com.example.deusto_hotel.repository.CourtRepository;
import com.example.deusto_hotel.service.CourtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourtServiceImpl implements CourtService {

    private final CourtRepository courtRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CourtResponse> findAll() {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional(readOnly = true)
    public CourtResponse findById(Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    public CourtResponse create(CourtRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    public CourtResponse update(Long id, CourtRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }
}
