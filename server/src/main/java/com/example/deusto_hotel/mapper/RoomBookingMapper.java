package com.example.deusto_hotel.mapper;

import com.example.deusto_hotel.dto.RoomBookingResponse;
import com.example.deusto_hotel.model.RoomBooking;
import org.mapstruct.*;

/**
 * Mapeador para convertir entre entidades RoomBooking y DTOs de solicitud/respuesta.
 * Utiliza MapStruct para generar las implementaciones de mapeo automáticamente.
 */
@Mapper(componentModel = "spring")
public interface RoomBookingMapper {
    //  Entity → DTO de respuesta
    /**
     * Convierte una entidad RoomBooking a un DTO de respuesta.
     * Mapea los datos de la reserva, cliente y habitación al DTO de respuesta.
     * @param booking La entidad RoomBooking a convertir
     * @return El DTO de respuesta mapeado con los datos de la reserva
     */
    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "cliente.nombre", target = "clienteNombre")
    @Mapping(source = "habitacion.id", target = "habitacionId")
    @Mapping(source = "habitacion.numero", target = "habitacionNumero")
    RoomBookingResponse toResponse(RoomBooking booking);


}

