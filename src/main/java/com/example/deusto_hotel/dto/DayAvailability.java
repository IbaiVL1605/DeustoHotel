package com.example.deusto_hotel.dto;

import java.time.LocalDate;
import java.util.List;

public record DayAvailability(LocalDate date, List<AvailableSlot> slots) {}
