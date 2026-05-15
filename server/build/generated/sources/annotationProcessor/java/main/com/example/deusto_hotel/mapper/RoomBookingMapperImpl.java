package com.example.deusto_hotel.mapper;

import com.example.deusto_hotel.dto.RoomBookingResponse;
import com.example.deusto_hotel.model.Room;
import com.example.deusto_hotel.model.RoomBooking;
import com.example.deusto_hotel.model.RoomBookingStatus;
import com.example.deusto_hotel.model.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-25T10:55:32+0200",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.1.jar, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class RoomBookingMapperImpl implements RoomBookingMapper {

    @Override
    public RoomBookingResponse toResponse(RoomBooking booking) {
        if ( booking == null ) {
            return null;
        }

        Long clienteId = null;
        String clienteNombre = null;
        Long habitacionId = null;
        String habitacionNumero = null;
        Long id = null;
        LocalDate checkIn = null;
        LocalDate checkOut = null;
        RoomBookingStatus estado = null;
        Double precioTotal = null;
        LocalDateTime creadaEn = null;

        clienteId = bookingClienteId( booking );
        clienteNombre = bookingClienteNombre( booking );
        habitacionId = bookingHabitacionId( booking );
        habitacionNumero = bookingHabitacionNumero( booking );
        id = booking.getId();
        checkIn = booking.getCheckIn();
        checkOut = booking.getCheckOut();
        estado = booking.getEstado();
        if ( booking.getPrecioTotal() != null ) {
            precioTotal = booking.getPrecioTotal().doubleValue();
        }
        creadaEn = booking.getCreadaEn();

        RoomBookingResponse roomBookingResponse = new RoomBookingResponse( id, clienteId, clienteNombre, habitacionId, habitacionNumero, checkIn, checkOut, estado, precioTotal, creadaEn );

        return roomBookingResponse;
    }

    private Long bookingClienteId(RoomBooking roomBooking) {
        if ( roomBooking == null ) {
            return null;
        }
        User cliente = roomBooking.getCliente();
        if ( cliente == null ) {
            return null;
        }
        Long id = cliente.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String bookingClienteNombre(RoomBooking roomBooking) {
        if ( roomBooking == null ) {
            return null;
        }
        User cliente = roomBooking.getCliente();
        if ( cliente == null ) {
            return null;
        }
        String nombre = cliente.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }

    private Long bookingHabitacionId(RoomBooking roomBooking) {
        if ( roomBooking == null ) {
            return null;
        }
        Room habitacion = roomBooking.getHabitacion();
        if ( habitacion == null ) {
            return null;
        }
        Long id = habitacion.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String bookingHabitacionNumero(RoomBooking roomBooking) {
        if ( roomBooking == null ) {
            return null;
        }
        Room habitacion = roomBooking.getHabitacion();
        if ( habitacion == null ) {
            return null;
        }
        String numero = habitacion.getNumero();
        if ( numero == null ) {
            return null;
        }
        return numero;
    }
}
