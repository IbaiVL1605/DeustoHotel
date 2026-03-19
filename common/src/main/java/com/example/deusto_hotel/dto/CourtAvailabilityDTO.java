package com.example.deusto_hotel.dto;

import com.example.deusto_hotel.model.CourtType;

import java.util.List;

public record CourtAvailabilityDTO(CourtType tipo, List<CourtBookingResponse> reservas) {}
