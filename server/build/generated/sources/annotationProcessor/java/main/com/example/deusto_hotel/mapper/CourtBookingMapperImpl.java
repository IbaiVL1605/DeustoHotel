package com.example.deusto_hotel.mapper;

import com.example.deusto_hotel.dto.CourtBookingRequest;
import com.example.deusto_hotel.dto.CourtBookingResponse;
import com.example.deusto_hotel.model.Court;
import com.example.deusto_hotel.model.CourtBooking;
import com.example.deusto_hotel.model.CourtBookingStatus;
import com.example.deusto_hotel.model.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-25T10:55:32+0200",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.1.jar, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class CourtBookingMapperImpl implements CourtBookingMapper {

    @Override
    public CourtBookingResponse toResponse(CourtBooking booking) {
        if ( booking == null ) {
            return null;
        }

        Long clienteId = null;
        String clienteNombre = null;
        Long pistaId = null;
        String pistaNombre = null;
        Long id = null;
        LocalDate fecha = null;
        LocalTime horaInicio = null;
        LocalTime horaFin = null;
        CourtBookingStatus estado = null;
        Double precioTotal = null;
        LocalDateTime creadaEn = null;

        clienteId = bookingClienteId( booking );
        clienteNombre = bookingClienteNombre( booking );
        pistaId = bookingPistaId( booking );
        pistaNombre = bookingPistaNombre( booking );
        id = booking.getId();
        fecha = booking.getFecha();
        horaInicio = booking.getHoraInicio();
        horaFin = booking.getHoraFin();
        estado = booking.getEstado();
        precioTotal = booking.getPrecioTotal();
        creadaEn = booking.getCreadaEn();

        CourtBookingResponse courtBookingResponse = new CourtBookingResponse( id, clienteId, clienteNombre, pistaId, pistaNombre, fecha, horaInicio, horaFin, estado, precioTotal, creadaEn );

        return courtBookingResponse;
    }

    @Override
    public CourtBooking toEntity(CourtBookingRequest request) {
        if ( request == null ) {
            return null;
        }

        CourtBooking courtBooking = new CourtBooking();

        courtBooking.setFecha( request.fecha() );
        courtBooking.setHoraInicio( request.horaInicio() );
        courtBooking.setHoraFin( request.horaFin() );

        return courtBooking;
    }

    @Override
    public void updateEntityFromRequest(CourtBookingRequest request, CourtBooking booking) {
        if ( request == null ) {
            return;
        }

        booking.setFecha( request.fecha() );
        booking.setHoraInicio( request.horaInicio() );
        booking.setHoraFin( request.horaFin() );
    }

    private Long bookingClienteId(CourtBooking courtBooking) {
        if ( courtBooking == null ) {
            return null;
        }
        User cliente = courtBooking.getCliente();
        if ( cliente == null ) {
            return null;
        }
        Long id = cliente.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String bookingClienteNombre(CourtBooking courtBooking) {
        if ( courtBooking == null ) {
            return null;
        }
        User cliente = courtBooking.getCliente();
        if ( cliente == null ) {
            return null;
        }
        String nombre = cliente.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }

    private Long bookingPistaId(CourtBooking courtBooking) {
        if ( courtBooking == null ) {
            return null;
        }
        Court pista = courtBooking.getPista();
        if ( pista == null ) {
            return null;
        }
        Long id = pista.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String bookingPistaNombre(CourtBooking courtBooking) {
        if ( courtBooking == null ) {
            return null;
        }
        Court pista = courtBooking.getPista();
        if ( pista == null ) {
            return null;
        }
        String nombre = pista.getNombre();
        if ( nombre == null ) {
            return null;
        }
        return nombre;
    }
}
