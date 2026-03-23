package com.example.deusto_hotel.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeekAvailability(
        @JsonProperty("weekNumber") int weekNumber,
        @JsonProperty("days") List<DayAvailability> days) {}
