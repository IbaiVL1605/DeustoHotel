package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.dto.RoomBookingResponse;
import com.example.deusto_hotel.model.Room;
import com.example.deusto_hotel.repository.RoomBookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomBookingService {

    private final RoomBookingRepository roomBookingRepository;

    
    @Transactional(readOnly = true)
    public List<RoomBookingResponse> findAll() {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    @Transactional(readOnly = true)
    public RoomBookingResponse findById(Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    public RoomBookingResponse create(RoomBookingRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    public RoomBookingResponse update(Long id, RoomBookingRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    public void delete(Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    @Transactional(readOnly = true)
    public List<RoomBookingResponse> findByClienteId(Long clienteId) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    @Transactional(readOnly = true)
    public List<RoomBookingResponse> findByHabitacionId(Long habitacionId) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }


}
