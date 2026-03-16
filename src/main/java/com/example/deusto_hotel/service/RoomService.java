package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.RoomDisponiblesResponse;
import com.example.deusto_hotel.dto.RoomRequest;
import com.example.deusto_hotel.dto.RoomResponse;
import com.example.deusto_hotel.mapper.RoomMapper;
import com.example.deusto_hotel.model.Room;
import com.example.deusto_hotel.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    
    @Transactional(readOnly = true)
    public List<RoomResponse> findAll() {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    @Transactional(readOnly = true)
    public RoomResponse findById(Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    public RoomResponse create(RoomRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    public RoomResponse update(Long id, RoomRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    public void delete(Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    public List<RoomDisponiblesResponse> getDisponibles(LocalDate fechaEntrada, LocalDate fechaSalida) {

        List<Room> disponibles = roomRepository.findRoomDisponibles(fechaEntrada, fechaSalida);

        return roomMapper.toRoomDisponiblesResponse(disponibles);
    }
}
