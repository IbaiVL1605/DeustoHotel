package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.dto.RoomDisponibleResponse;
import com.example.deusto_hotel.dto.RoomDisponiblesSimplesResponse;
import com.example.deusto_hotel.dto.RoomDisponiblesSuitResponse;
import com.example.deusto_hotel.mapper.RoomMapper;
import com.example.deusto_hotel.mapper.RoomMapperImpl;
import com.example.deusto_hotel.model.Room;
import com.example.deusto_hotel.model.RoomType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class RoomMapperTest {

    private final RoomMapper mapper = new RoomMapperImpl();

    @Test
    public void testToRoomDisponiblesResponse_WhenInputIsNull(){
        List<RoomDisponibleResponse> response = mapper.toRoomDisponiblesResponse(null);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(0, response.size());

    }

    @Test
    public void testToRoomDisponiblesResponse_WhenInputIsEmpty(){
        List<RoomDisponibleResponse> response = mapper.toRoomDisponiblesResponse(new ArrayList<>());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(0, response.size());
    }

    @Test
    public void testToRoomDisponiblesResponse_WhenInputIsCorrect(){
        List<Room> rooms = List.of(
                new Room(1L, RoomType.SUITE),
                new Room(2L, RoomType.SUITE),
                new Room(3L, RoomType.INDIVIDUAL),
                new Room(4L, RoomType.INDIVIDUAL),
                new Room(5L, RoomType.DOBLE)
        );

        List<RoomDisponibleResponse> response = mapper.toRoomDisponiblesResponse(rooms);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(3, response.size());

        response.forEach(r -> {;
            if (r instanceof RoomDisponiblesSuitResponse){
                RoomDisponiblesSuitResponse suitResponse = (RoomDisponiblesSuitResponse) r;
                Assertions.assertEquals(RoomType.SUITE, suitResponse.getTipo());
                Assertions.assertEquals(2, suitResponse.getSuites().size());
            } else if (r instanceof RoomDisponiblesSimplesResponse){
                RoomDisponiblesSimplesResponse simplesResponse = (RoomDisponiblesSimplesResponse) r;
                if (simplesResponse.getTipo().equals(RoomType.INDIVIDUAL)){
                    Assertions.assertEquals(2, simplesResponse.getNumero_disponibles());
                } else if (simplesResponse.getTipo().equals(RoomType.DOBLE)){
                    Assertions.assertEquals(1, simplesResponse.getNumero_disponibles());
                } else {
                    Assertions.fail("Tipo de habitación no esperado: " + simplesResponse.getTipo());
                }
            } else {
                Assertions.fail("Tipo de respuesta no esperado: " + r.getClass().getName());
            }
        });

    }
}
