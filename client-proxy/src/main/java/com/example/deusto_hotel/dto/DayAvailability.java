package com.example.deusto_hotel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;

public record DayAvailability(
    @JsonProperty("date") LocalDate fecha,
    List<AvailableSlot> slots
) {}
