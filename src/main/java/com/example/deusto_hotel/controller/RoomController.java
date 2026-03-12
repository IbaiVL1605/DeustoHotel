package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.RoomRequest;
import com.example.deusto_hotel.dto.RoomResponse;
import com.example.deusto_hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAll() {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getById(@PathVariable Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @PostMapping
    public ResponseEntity<RoomResponse> create(@RequestBody @Valid RoomRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid RoomRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }
}
