package com.example.deusto_hotel.dto;

import java.util.List;

public record CourtDayAvailability(Long courtId, String nombre, String tipo, Double precioPorHora, String estado, List<AvailableSlot> slots) {}
