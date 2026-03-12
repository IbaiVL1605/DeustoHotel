package com.example.deusto_hotel.service.impl;

import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.dto.RoomBookingResponse;
import com.example.deusto_hotel.repository.RoomBookingRepository;
import com.example.deusto_hotel.service.RoomBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomBookingServiceImpl implements RoomBookingService {

    private final RoomBookingRepository roomBookingRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoomBookingResponse> findAll() {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomBookingResponse findById(Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    public RoomBookingResponse create(RoomBookingRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    public RoomBookingResponse update(Long id, RoomBookingRequest request) {
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
    public List<RoomBookingResponse> findByClienteId(Long clienteId) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomBookingResponse> findByHabitacionId(Long habitacionId) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }
}
