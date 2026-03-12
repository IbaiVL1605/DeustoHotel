package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.CourtRequest;
import com.example.deusto_hotel.dto.CourtResponse;

import java.util.List;

public interface CourtService {

    List<CourtResponse> findAll();

    CourtResponse findById(Long id);

    CourtResponse create(CourtRequest request);

    CourtResponse update(Long id, CourtRequest request);

    void delete(Long id);
}
