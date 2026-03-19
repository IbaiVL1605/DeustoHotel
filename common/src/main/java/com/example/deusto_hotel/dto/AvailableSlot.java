package com.example.deusto_hotel.dto;

import java.time.LocalTime;
import java.util.List;

public record AvailableSlot(LocalTime start, LocalTime end, List<CourtResponse> availableCourts) {}
