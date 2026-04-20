package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.RoomDisponibleResponse;
import com.example.deusto_hotel.dto.RoomRequest;
import com.example.deusto_hotel.dto.RoomResponse;
import com.example.deusto_hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAll() {
        return ResponseEntity.ok(roomService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.findById(id));
    }

    @PostMapping
    public ResponseEntity<RoomResponse> create(
            @RequestBody @Valid RoomRequest request,
            jakarta.servlet.http.HttpSession session
    ) {
        RoomResponse created = roomService.create(request);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid RoomRequest request) {

        return ResponseEntity.ok(roomService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/disponibles")
    public ResponseEntity<List<RoomDisponibleResponse>> getByDisponibles(
            @RequestParam LocalDate fechaEntrada,
            @RequestParam LocalDate fechaSalida) {

        if(fechaEntrada.isAfter(fechaSalida) || fechaEntrada.isEqual(fechaSalida)) {
            throw new IllegalArgumentException("La fecha de salida debe de ser posterior a la fecha de entrada.");

        } else if(fechaEntrada.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de entrada no puede ser anterior a la fecha actual.");

        }

        return ResponseEntity.ok(
                roomService.getDisponibles(fechaEntrada, fechaSalida)
        );
    }
}
