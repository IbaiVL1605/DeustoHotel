package com.example.deusto_hotel.mapper;

import com.example.deusto_hotel.dto.CourtBookingRequest;
import com.example.deusto_hotel.dto.CourtBookingResponse;
import com.example.deusto_hotel.model.CourtBooking;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CourtBookingMapper {

    // Entity → DTO
    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "cliente.nombre", target = "clienteNombre")
    @Mapping(source = "pista.id", target = "pistaId")
    @Mapping(source = "pista.nombre", target = "pistaNombre")
    CourtBookingResponse toResponse(CourtBooking booking);

    // DTO → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creadaEn", ignore = true)
    @Mapping(target = "cliente", ignore = true)      // se setea en service
    @Mapping(target = "pista", ignore = true)        // se setea en service
    @Mapping(target = "precioTotal", ignore = true)  // se calcula
    @Mapping(target = "estado", ignore = true)       // default = PENDIENTE
    CourtBooking toEntity(CourtBookingRequest request);

    // Update entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creadaEn", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "pista", ignore = true)
    @Mapping(target = "precioTotal", ignore = true)
    @Mapping(target = "estado", ignore = true)
    void updateEntityFromRequest(CourtBookingRequest request,
                                 @MappingTarget CourtBooking booking);
}
