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

    //  DTO de petición → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creadaEn", ignore = true)
    @Mapping(target = "cliente", ignore = true)      // se setea en el service
    @Mapping(target = "habitacion", ignore = true)   // se setea en el service
    @Mapping(target = "precioTotal", ignore = true)  // se calcula
    @Mapping(target = "estado", ignore = true)       // valor por defecto
    RoomBooking toEntity(RoomBookingRequest request);

    //  Actualizar Entity existente
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creadaEn", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "habitacion", ignore = true)
    @Mapping(target = "precioTotal", ignore = true)
    @Mapping(target = "estado", ignore = true)
    void updateEntityFromRequest(RoomBookingRequest request, @MappingTarget RoomBooking booking);
}

