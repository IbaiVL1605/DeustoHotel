package com.example.deusto_hotel.mapper;

import com.example.deusto_hotel.dto.SuitResponse;
import com.example.deusto_hotel.model.Room;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-25T10:55:32+0200",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.1.jar, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class RoomMapperImpl extends RoomMapper {

    @Override
    public SuitResponse suitToSuitResponse(Room room) {
        if ( room == null ) {
            return null;
        }

        int capacidad = 0;
        int precioPorNoche = 0;
        int id = 0;

        capacidad = room.getCapacidad();
        precioPorNoche = room.getPrecioPorNoche();
        if ( room.getId() != null ) {
            id = room.getId().intValue();
        }

        SuitResponse suitResponse = new SuitResponse( capacidad, precioPorNoche, id );

        return suitResponse;
    }
}
