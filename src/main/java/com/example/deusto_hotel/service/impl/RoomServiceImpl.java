package com.example.deusto_hotel.service.impl;

import com.example.deusto_hotel.dto.RoomRequest;
import com.example.deusto_hotel.dto.RoomResponse;
import com.example.deusto_hotel.repository.RoomRepository;
import com.example.deusto_hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> findAll() {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse findById(Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    public RoomResponse create(RoomRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    public RoomResponse update(Long id, RoomRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }
}
