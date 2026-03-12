package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.CourtBookingRequest;
import com.example.deusto_hotel.dto.CourtBookingResponse;

import java.util.List;

public interface CourtBookingService {

    List<CourtBookingResponse> findAll();

    CourtBookingResponse findById(Long id);

    CourtBookingResponse create(CourtBookingRequest request);

    CourtBookingResponse update(Long id, CourtBookingRequest request);

    void delete(Long id);

    List<CourtBookingResponse> findByClienteId(Long clienteId);

    List<CourtBookingResponse> findByPistaId(Long pistaId);
}
