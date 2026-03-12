package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.dto.RoomBookingResponse;

import java.util.List;

public interface RoomBookingService {

    List<RoomBookingResponse> findAll();

    RoomBookingResponse findById(Long id);

    RoomBookingResponse create(RoomBookingRequest request);

    RoomBookingResponse update(Long id, RoomBookingRequest request);

    void delete(Long id);

    List<RoomBookingResponse> findByClienteId(Long clienteId);

    List<RoomBookingResponse> findByHabitacionId(Long habitacionId);
}
