package com.example.deusto_hotel.mapper;

import com.example.deusto_hotel.dto.RoomDisponibleResponse;
import com.example.deusto_hotel.dto.RoomDisponiblesSimplesResponse;
import com.example.deusto_hotel.dto.RoomDisponiblesSuitResponse;
import com.example.deusto_hotel.dto.SuitResponse;
import com.example.deusto_hotel.model.Room;
import com.example.deusto_hotel.model.RoomType;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapper para la transformación de entidades {@link Room} a objetos DTO.
 *
 * Proporciona métodos para convertir habitaciones en respuestas disponibles,
 * agrupándolas por tipo (Suites simples) y aplicando lógica de negocio
 * según el tipo de habitación.
 *
 * @author DeustoHotel
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public abstract class RoomMapper {

    /**
     * Convierte una entidad {@link Room} de tipo Suite a su correspondiente {@link SuitResponse}.
     *
     * Este método es abstracto y la implementación se genera automáticamente por MapStruct.
     *
     * @param room la habitación de tipo Suite a convertir
     * @return {@link SuitResponse} con los datos de la suite convertidos, o null si room es null
     */
    public abstract SuitResponse suitToSuitResponse(Room room);

    /**
     * Convierte una lista de habitaciones disponibles en respuestas agrupadas por tipo.
     *
     * Agrupa las habitaciones por su tipo ({@link RoomType}). Para las suites, crea una
     * lista de {@link SuitResponse}. Para otros tipos de habitaciones, solo cuenta la
     * cantidad disponible.
     *
     * @param rooms lista de habitaciones disponibles a convertir
     * @return lista de {@link RoomDisponibleResponse} agrupadas y formateadas por tipo.
     *         Retorna una lista vacía si el parámetro rooms es null.
     */
    public List<RoomDisponibleResponse> toRoomDisponiblesResponse(List<Room> rooms) {

        if (rooms == null) {
            return new ArrayList<>();
        }

        Map<RoomType, List<Room>> map = rooms.stream().collect(
                java.util.stream.Collectors.groupingBy(Room::getTipo)
        );

        List<RoomDisponibleResponse> response = new ArrayList<>();

        map.forEach((tipo, disponibles) -> {
            if (tipo.equals(RoomType.SUITE)){
                List<SuitResponse> suits = disponibles.stream().map(this::suitToSuitResponse).toList();
                response.add(new RoomDisponiblesSuitResponse(tipo, suits));
            } else {
                response.add(new RoomDisponiblesSimplesResponse(tipo, disponibles.size()));
            }
        });

        return response;
    }


}
