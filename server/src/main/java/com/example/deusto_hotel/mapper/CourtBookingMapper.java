package com.example.deusto_hotel.mapper;

import com.example.deusto_hotel.dto.CourtBookingRequest;
import com.example.deusto_hotel.dto.CourtBookingResponse;
import com.example.deusto_hotel.model.CourtBooking;
import org.mapstruct.*;

/**
 * Mapper encargado de la conversión entre entidades
 * de reservas de pistas y sus correspondientes DTOs.
 * <p>
 * Utiliza MapStruct para automatizar las transformaciones
 * entre objetos de entrada, salida y entidades persistentes.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface CourtBookingMapper {

    /**
     * Convierte una entidad {@link CourtBooking}
     * en un DTO de respuesta.
     * <p>
     * También transforma información relacionada
     * del cliente y la pista asociada.
     * </p>
     *
     * @param booking entidad de reserva
     * @return DTO de respuesta de la reserva
     */
    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "cliente.nombre", target = "clienteNombre")
    @Mapping(source = "pista.id", target = "pistaId")
    @Mapping(source = "pista.nombre", target = "pistaNombre")
    CourtBookingResponse toResponse(CourtBooking booking);

    /**
     * Convierte un DTO de creación de reserva
     * en una entidad {@link CourtBooking}.
     * <p>
     * Algunos campos son ignorados porque se calculan
     * o asignan posteriormente en la capa de servicio.
     * </p>
     *
     * @param request datos de creación de la reserva
     * @return entidad de reserva
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creadaEn", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "pista", ignore = true)
    @Mapping(target = "precioTotal", ignore = true)
    @Mapping(target = "estado", ignore = true)
    CourtBooking toEntity(CourtBookingRequest request);

    /**
     * Actualiza una entidad de reserva existente
     * utilizando los datos proporcionados en el DTO.
     * <p>
     * Algunos atributos se ignoran para evitar
     * sobrescribir valores gestionados por el sistema.
     * </p>
     *
     * @param request datos actualizados de la reserva
     * @param booking entidad de reserva a actualizar
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creadaEn", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "pista", ignore = true)
    @Mapping(target = "precioTotal", ignore = true)
    @Mapping(target = "estado", ignore = true)
    void updateEntityFromRequest(
            CourtBookingRequest request,
            @MappingTarget CourtBooking booking
    );
}
