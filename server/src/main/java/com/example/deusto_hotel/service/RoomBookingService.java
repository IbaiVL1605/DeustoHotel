package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.dto.RoomBookingResponse;
import com.example.deusto_hotel.mapper.RoomBookingMapper;
import com.example.deusto_hotel.model.Room;
import com.example.deusto_hotel.model.RoomBooking;
import com.example.deusto_hotel.model.RoomType;
import com.example.deusto_hotel.model.User;
import com.example.deusto_hotel.repository.RoomBookingRepository;
import com.example.deusto_hotel.repository.UserRepository;
import com.example.deusto_hotel.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomBookingService {

    private final RoomBookingRepository roomBookingRepository;
    private final RoomBookingMapper roomBookingMapper;
    private final RoomRepository roomRepository;
    private  final UserRepository userRepository;



    // Crear reserva
    @Transactional()
    public void create(List<RoomBookingRequest> request) {

        for  (RoomBookingRequest roomBookingRequest : request) {
            validarFechas(roomBookingRequest);
            User cliente = userRepository.findById(roomBookingRequest.id_cliente())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

            if (roomBookingRequest.tipo().equals(RoomType.INDIVIDUAL) || roomBookingRequest.tipo().equals(RoomType.DOBLE)) {
                reservarSimples(roomBookingRequest, cliente);
            } else if(roomBookingRequest.tipo().equals(RoomType.SUITE)) {
                reservarSuits(roomBookingRequest, cliente);
            } else {
                throw new IllegalArgumentException("Tipo de Reserva no encontrada");
            }
        }


    }

    private void reservarSuits(RoomBookingRequest roomBookingRequest, User cliente) {
        System.out.printf("Reserva SUITE: %s\n", roomBookingRequest.id_habitacion());
        Optional<Room> habitacion = roomRepository.findByIdAndTipo(roomBookingRequest.id_habitacion(), RoomType.SUITE);
        if(habitacion.isEmpty()) {throw new IllegalArgumentException("Habitacion no encontrada para tipo SUITE");}

        RoomBooking reserva = new RoomBooking();
        reserva.setCliente(cliente);
        reserva.setHabitacion(habitacion.get());
        reserva.setCheckIn(roomBookingRequest.fechaEntrada());
        reserva.setCheckOut(roomBookingRequest.fechaSalida());
        reserva.setPrecioTotal(habitacion.get().getPrecioPorNoche());

        roomBookingRepository.save(reserva);
    }

    private void validarFechas(RoomBookingRequest roomBookingRequest) {
        if(roomBookingRequest.fechaEntrada().isAfter(roomBookingRequest.fechaSalida()) || roomBookingRequest.fechaEntrada().isEqual(roomBookingRequest.fechaSalida())) {
            throw new IllegalArgumentException("La fecha de salida debe de ser posterior a la fecha de entrada.");

        } else if(roomBookingRequest.fechaEntrada().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de entrada no puede ser anterior a la fecha actual.");

        }

    }

    private void reservarSimples(RoomBookingRequest roomBookingRequest, User cliente) {

        List<Room> disponiblesTotal = roomRepository.findRoomDisponibles(roomBookingRequest.fechaEntrada(), roomBookingRequest.fechaSalida());
        if(disponiblesTotal.isEmpty()) {throw new IllegalArgumentException("No hay habitaciones disponibles para las fechas seleccionadas");}

        List<Room> disponibles = disponiblesTotal.stream()
                .filter(h -> h.getTipo().equals(roomBookingRequest.tipo()))
                .toList();


        if(disponibles.size() < roomBookingRequest.cantidad()) {throw new IllegalArgumentException(String.format("No hay habitaciones disponibles para las fechas seleccionadas con el tipo: %s", roomBookingRequest.tipo()));}

        for (int i = 0; i < roomBookingRequest.cantidad(); i++) {
            RoomBooking reserva = new RoomBooking();

            reserva.setCliente(cliente);
            reserva.setCheckIn(roomBookingRequest.fechaEntrada());
            reserva.setCheckOut(roomBookingRequest.fechaSalida());


            Room habitacion = disponibles.get(i);
            reserva.setPrecioTotal(habitacion.getPrecioPorNoche());
            reserva.setHabitacion(habitacion);


            roomBookingRepository.save(reserva);
        }






    }



    /*
    // Actualizar reserva
    public RoomBookingResponse update(Long id, RoomBookingRequest request) {



        RoomBooking booking = roomBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        Room room = roomRepository.findById(request.habitacionId())
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

        roomBookingMapper.updateEntityFromRequest(request, booking);

        booking.setHabitacion(room);
        booking.setPrecioTotal(calcularPrecio(room, request.checkIn(), request.checkOut()));

        return roomBookingMapper.toResponse(booking);
    }
    
*/

//  Eliminar reserva
public void delete(Long id, Long userId) {

    RoomBooking booking = roomBookingRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Reserva no encontrada"));

    if (userId == null) {
        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
    }

    if (!booking.getCliente().getId().equals(userId)) {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "No puedes cancelar esta reserva");
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

    /*
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
    */


}
