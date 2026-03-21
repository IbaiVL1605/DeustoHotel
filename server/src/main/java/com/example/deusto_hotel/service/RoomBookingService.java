package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.dto.RoomBookingResponse;
import com.example.deusto_hotel.mapper.RoomBookingMapper;
import com.example.deusto_hotel.model.Room;
import com.example.deusto_hotel.model.RoomBooking;
import com.example.deusto_hotel.model.User;
import com.example.deusto_hotel.repository.RoomBookingRepository;
import com.example.deusto_hotel.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomBookingService {

    private final RoomBookingRepository roomBookingRepository;
    private final RoomBookingMapper roomBookingMapper;
    private final RoomRepository roomRepository;

    //  Obtener todas las reservas
    @Transactional(readOnly = true)
    public List<RoomBookingResponse> findAll() {
        return roomBookingRepository.findAll()
                .stream()
                .map(roomBookingMapper::toResponse)
                .toList();
    }

    //  Obtener por ID
    @Transactional(readOnly = true)
    public RoomBookingResponse findById(Long id) {
        RoomBooking booking = roomBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        return roomBookingMapper.toResponse(booking);
    }

    // 🔹 Crear reserva
    public RoomBookingResponse create(RoomBookingRequest request) {

        Room room = roomRepository.findById(request.habitacionId())
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));



        RoomBooking booking = roomBookingMapper.toEntity(request);

        booking.setHabitacion(room);
        // Preguntar como obtener el cliente que hace la reserva
        booking.setPrecioTotal(calcularPrecio(room, request.checkIn(), request.checkOut()));

        roomBookingRepository.save(booking);

        return roomBookingMapper.toResponse(booking);
    }

    // 🔹 Actualizar reserva
    public RoomBookingResponse update(Long id, RoomBookingRequest request) {

        validarFechas(request.checkIn(), request.checkOut());

        RoomBooking booking = roomBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        Room room = roomRepository.findById(request.habitacionId())
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

        roomBookingMapper.updateEntityFromRequest(request, booking);

        booking.setHabitacion(room);
        booking.setPrecioTotal(calcularPrecio(room, request.checkIn(), request.checkOut()));

        return roomBookingMapper.toResponse(booking);
    }

    //  Eliminar reserva
    public void delete(Long id) {
        if (!roomBookingRepository.existsById(id)) {
            throw new RuntimeException("Reserva no encontrada");
        }

        roomBookingRepository.deleteById(id);
    }

    //  Buscar por cliente
    @Transactional(readOnly = true)
    public List<RoomBookingResponse> findByClienteId(Long clienteId) {
        return roomBookingRepository.findByClienteId(clienteId)
                .stream()
                .map(roomBookingMapper::toResponse)
                .toList();
    }

    //  Buscar por habitación
    @Transactional(readOnly = true)
    public List<RoomBookingResponse> findByHabitacionId(Long habitacionId) {
        return roomBookingRepository.findByHabitacionId(habitacionId)
                .stream()
                .map(roomBookingMapper::toResponse)
                .toList();
    }
    private Double calcularPrecio(Room room, java.time.LocalDate checkIn, java.time.LocalDate checkOut) {
        long dias = ChronoUnit.DAYS.between(checkIn, checkOut);
        return (double) (dias * room.getPrecioPorNoche());
    }



}
