package com.example.deusto_hotel.service.impl;

import com.example.deusto_hotel.dto.CourtBookingRequest;
import com.example.deusto_hotel.dto.CourtBookingResponse;
import com.example.deusto_hotel.repository.CourtBookingRepository;
import com.example.deusto_hotel.service.CourtBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CourtBookingServiceImpl implements CourtBookingService {

    private final CourtBookingRepository courtBookingRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CourtBookingResponse> findAll() {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional(readOnly = true)
    public CourtBookingResponse findById(Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    public CourtBookingResponse create(CourtBookingRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    public CourtBookingResponse update(Long id, CourtBookingRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourtBookingResponse> findByClienteId(Long clienteId) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourtBookingResponse> findByPistaId(Long pistaId) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }
}
