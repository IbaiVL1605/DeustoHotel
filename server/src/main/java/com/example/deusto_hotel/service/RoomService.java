package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.RoomDisponibleResponse;
import com.example.deusto_hotel.dto.RoomRequest;
import com.example.deusto_hotel.dto.RoomResponse;
import com.example.deusto_hotel.mapper.RoomMapper;
import com.example.deusto_hotel.model.Room;
import com.example.deusto_hotel.model.RoomStatus;
import com.example.deusto_hotel.model.RoomType;
import com.example.deusto_hotel.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;



    @Transactional(readOnly = true)
    public List<RoomDisponibleResponse> getDisponibles(LocalDate fechaEntrada, LocalDate fechaSalida) {

        List<Room> disponibles = roomRepository.findRoomDisponibles(fechaEntrada, fechaSalida);

        return roomMapper.toRoomDisponiblesResponse(disponibles);
    }
    public RoomResponse create(RoomRequest request) {

        if (roomRepository.existsByNumero(request.numero())) {
            throw new IllegalArgumentException("Ya existe una habitación con ese número");
        }

        Room room = new Room();

        room.setTipo(request.tipo());
        room.setNumero(request.numero());

        if (request.tipo() == RoomType.SUITE) {
            room.setCapacidad(request.capacidad());
            room.setPrecioPorNoche(request.precioPorNoche().intValue());
        }else if (request.tipo() == RoomType.INDIVIDUAL) {
        } else if (request.tipo() == RoomType.DOBLE) {
        }else {
            throw new IllegalArgumentException("Tipo de habitación no válido");
        }

        Room saved = roomRepository.save(room);

        return new RoomResponse(
                saved.getId(),
                saved.getNumero(),
                saved.getTipo(),
                saved.getCapacidad(),
                (double) saved.getPrecioPorNoche(),
                saved.getEstado()
        );
    }

    public void delete(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Habitación no encontrada");
        }
        roomRepository.deleteById(id);
    }

    public void bloquearHabitacion(Long id) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Habitación no encontrada"));

        room.setEstado(RoomStatus.BLOQUEADA);

        roomRepository.save(room);
    }

}


