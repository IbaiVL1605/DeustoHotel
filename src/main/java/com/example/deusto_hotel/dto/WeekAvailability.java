package com.example.deusto_hotel.dto;

import java.util.List;

public record WeekAvailability(int weekNumber, List<DayAvailability> days) {}
