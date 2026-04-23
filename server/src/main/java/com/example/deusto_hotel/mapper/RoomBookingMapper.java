package com.example.deusto_hotel.mapper;

import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.dto.RoomBookingResponse;
import com.example.deusto_hotel.model.RoomBooking;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoomBookingMapper {
    //  Entity → DTO de respuesta
    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "cliente.nombre", target = "clienteNombre")
    @Mapping(source = "habitacion.id", target = "habitacionId")
    @Mapping(source = "habitacion.numero", target = "habitacionNumero")
    RoomBookingResponse toResponse(RoomBooking booking);


}

