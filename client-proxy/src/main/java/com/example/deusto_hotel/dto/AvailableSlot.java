package com.example.deusto_hotel.dto;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;
import java.util.List;

public record AvailableSlot(
        @JsonFormat(pattern = "H:mm[:ss]")
        LocalTime start,

        @JsonFormat(pattern = "H:mm[:ss]")
        LocalTime end,

        List<CourtResponse> availableCourts) {}

