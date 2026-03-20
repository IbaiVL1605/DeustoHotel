package com.example.deusto_hotel.proxy;

import com.example.deusto_hotel.dto.*;
import com.example.deusto_hotel.model.RoomType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class Proxy {

    private final HttpClient httpClient =  HttpClient.newBuilder().build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ArrayList<RoomDisponibleResponse> parseRoomDisponibleResponse(String response) {
        JsonNode node = objectMapper.readTree(response);
        ArrayList<RoomDisponibleResponse> roomDisponibleResponses = new ArrayList<>();



        node.forEach(item -> {
            if (item.get("tipo").asText().equals(RoomType.SUITE.toString())) {

                JsonNode suitesNode = item.get("suites");

                List<SuitResponse> suites = StreamSupport
                        .stream(suitesNode.spliterator(), false)  // ArrayNode → Stream
                        .map(suit -> new SuitResponse(
                                suit.get("capacidad").asInt(),
                                suit.get("precioPorNoche").asInt()
                        ))
                        .collect(Collectors.toList());

                roomDisponibleResponses.add(new RoomDisponiblesSuitResponse(
                        RoomType.SUITE,
                        suites
                ));
            } else {
                roomDisponibleResponses.add(new RoomDisponiblesSimplesResponse(
                        RoomType.valueOf(item.get("tipo").asString()),
                        item.get("numero_disponibles").asInt()
                ));
            }
        });

        return roomDisponibleResponses;
    }

    public ArrayList<RoomDisponibleResponse> getHabitacionesDisponibles(LocalDate fechaEntrada, LocalDate fechaSalida) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(String.format("http://localhost:8080/api/v1/rooms/disponibles?fechaEntrada=%s&fechaSalida=%s",fechaEntrada.toString(), fechaSalida.toString())))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


        return parseRoomDisponibleResponse(response.body());
    }
}

