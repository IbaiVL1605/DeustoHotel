package com.example.deusto_hotel.dto;

import com.example.deusto_hotel.model.Role;

import java.time.LocalDateTime;

public record UserResponse(
    Long id,
    String nombre,
    String email,
    Role rol,
    boolean bloqueado,
    LocalDateTime creadoEn
) {}
