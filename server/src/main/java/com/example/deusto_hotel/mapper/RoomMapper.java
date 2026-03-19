package com.example.deusto_hotel.mapper;

import com.example.deusto_hotel.dto.RoomDisponiblesResponse;
import com.example.deusto_hotel.model.Room;
import com.example.deusto_hotel.model.RoomType;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    default List<RoomDisponiblesResponse> toRoomDisponiblesResponse(List<Room> rooms) {

        if (rooms == null) {return new ArrayList<>();}

        Map<RoomType, List<Room>> map = rooms.stream().collect(
                java.util.stream.Collectors.groupingBy(Room::getTipo)
        );

        List<RoomDisponiblesResponse> response = new ArrayList<>();

        map.forEach((tipo, disponibles) -> {
            response.add(new RoomDisponiblesResponse(tipo, disponibles.size()));
        });

        return response;
    }
}
