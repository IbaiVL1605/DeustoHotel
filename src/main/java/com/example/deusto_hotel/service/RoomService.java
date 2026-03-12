package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.RoomRequest;
import com.example.deusto_hotel.dto.RoomResponse;

import java.util.List;

public interface RoomService {

    List<RoomResponse> findAll();

    RoomResponse findById(Long id);

    RoomResponse create(RoomRequest request);

    RoomResponse update(Long id, RoomRequest request);

    void delete(Long id);
}
