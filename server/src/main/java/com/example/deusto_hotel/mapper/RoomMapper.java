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

@Mapper(componentModel = "spring")
public abstract class RoomMapper {

    public abstract SuitResponse suitToSuitResponse(Room room);

    public List<RoomDisponibleResponse> toRoomDisponiblesResponse(List<Room> rooms) {

        if (rooms == null) {return new ArrayList<>();}

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
