package com.example.deusto_hotel.proxy;

import com.example.deusto_hotel.dto.*;
import com.example.deusto_hotel.model.RoomType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class Proxy {

    private static final Logger log = LoggerFactory.getLogger(Proxy.class);
    private final HttpClient httpClient =  HttpClient.newBuilder().build();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private ArrayList<RoomDisponibleResponse> parseRoomDisponibleResponse(String response) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(response);
        ArrayList<RoomDisponibleResponse> roomDisponibleResponses = new ArrayList<>();



        node.forEach(item -> {
            if (item.get("tipo").asText().equals(RoomType.SUITE.toString())) {

                JsonNode suitesNode = item.get("suites");

                List<SuitResponse> suites = StreamSupport
                        .stream(suitesNode.spliterator(), false)  // ArrayNode → Stream
                        .map(suit -> new SuitResponse(
                                suit.get("capacidad").asInt(),
                                suit.get("precioPorNoche").asInt(),
                                suit.get("id").asInt()
                        ))
                        .collect(Collectors.toList());

                roomDisponibleResponses.add(new RoomDisponiblesSuitResponse(
                        RoomType.SUITE,
                        suites
                ));
            } else {
                roomDisponibleResponses.add(new RoomDisponiblesSimplesResponse(
                        RoomType.valueOf(item.get("tipo").asText()),
                        item.get("numero_disponibles").asInt()
                ));
            }
        });

        return roomDisponibleResponses;
    }

    public ArrayList<RoomDisponibleResponse> getHabitacionesDisponibles(LocalDate fechaEntrada, LocalDate fechaSalida) throws IOException, InterruptedException, JsonProcessingException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(String.format("http://localhost:8080/api/v1/rooms/disponibles?fechaEntrada=%s&fechaSalida=%s",fechaEntrada.toString(), fechaSalida.toString())))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


        return parseRoomDisponibleResponse(response.body());
    }

    public List<CourtResponse> getCourts(String tipo) throws IOException, InterruptedException, JsonProcessingException {
        String url = "http://localhost:8080/api/v1/courts";
        if (tipo != null && !tipo.trim().isEmpty()) {
            url += "?tipo=" + tipo;
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<List<CourtResponse>>() {});
    }

    public List<WeekAvailability> getCourtsWeeklyAvailability(int year, int month, String tipo) throws IOException, InterruptedException, JsonProcessingException {
        StringBuilder url = new StringBuilder(String.format("http://localhost:8080/api/v1/courts/weekly-availability?year=%d&month=%d", year, month));
        if (tipo != null && !tipo.trim().isEmpty()) {
            url.append("&tipo=").append(tipo);
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(url.toString()))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<List<WeekAvailability>>() {});
    }

    public List<?> getCourtsAvailable(String tipo, String fecha, Integer semana) throws IOException, InterruptedException, JsonProcessingException {
        StringBuilder url = new StringBuilder("http://localhost:8080/api/v1/courts/available?");
        if (tipo != null && !tipo.trim().isEmpty()) url.append("tipo=").append(tipo).append("&");
        if (fecha != null && !fecha.trim().isEmpty()) url.append("fecha=").append(fecha).append("&");
        if (semana != null) url.append("semana=").append(semana);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(url.toString()))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<List<?>>() {});
    }

    public UserResponse login(String email, String password) throws IOException, InterruptedException {
        String correo = URLEncoder.encode(email, StandardCharsets.UTF_8);
        String contrasena = URLEncoder.encode(password, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(String.format(
                        "http://localhost:8080/api/v1/users/login?correo=%s&contrasena=%s",
                        correo,
                        contrasena
                )))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode usuarioNode = root.get("usuario");
            if (usuarioNode == null || usuarioNode.isNull()) {
                throw new IllegalArgumentException("Respuesta de login invalida");
            }
            return objectMapper.treeToValue(usuarioNode, UserResponse.class);
        }

        String errorMessage = response.body() == null || response.body().isBlank()
                ? "No se pudo iniciar sesion"
                : response.body();
        throw new IllegalArgumentException(errorMessage);
    }
    public void createBookings(List<RoomBookingRequest> requests)
            throws IOException, InterruptedException {

        String url = "http://localhost:8080/api/v1/room-bookings";

        for (RoomBookingRequest r : requests) {

            String body = objectMapper.writeValueAsString(r);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Error creando reservas: " + response.body());
            }
        }
    }

    public void createCourtBooking(CourtBookingRequest request) throws IOException, InterruptedException {
        String url = "http://localhost:8080/api/v1/court-bookings";
        String body = objectMapper.writeValueAsString(request);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Error creando reserva de pista: " + response.body());
        }
    }
    public RoomBookingResponse updateRoomBooking(Long id, RoomBookingRequest request)
            throws IOException, InterruptedException {

        String requestBody = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:8080/api/v1/room-bookings/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), RoomBookingResponse.class);
        }

        throw new RuntimeException("Error al actualizar la reserva: " + response.body());
    }

    public void deleteRoomBooking(Long id, Long userId)
            throws IOException, InterruptedException {

        String url = "http://localhost:8080/api/v1/room-bookings/"
                + id + "?userId=" + userId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Error al eliminar la reserva: " + response.body());
        }
    }

    public void signup(String nombre, String email, String password) {
        try {
            String encodedNombre = URLEncoder.encode(nombre, StandardCharsets.UTF_8);
            String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
            String encodedPassword = URLEncoder.encode(password, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(String.format(
                            "http://localhost:8080/api/v1/users?email=%s&password=%s&nombre=%s",
                            encodedEmail,
                            encodedPassword,
                            encodedNombre
                    )))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String errorMessage = response.body() == null || response.body().isBlank()
                        ? "No se pudo registrar el usuario"
                        : response.body();
                throw new IllegalArgumentException(errorMessage);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error al registrar el usuario", e);
        }
    }

    public void crearHabitacion(RoomRequest request) {
        try {
            System.out.println("REQUEST RECIBIDA DESDE CONTROLLER: " + request);
            String requestBody = objectMapper.writeValueAsString(request);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/rooms"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println("RESPONSE DEL SERVIDOR: " + response.statusCode() + " - " + response.body());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String errorMessage = response.body() == null || response.body().isBlank()
                        ? "No se pudo crear la habitación"
                        : response.body();
                throw new IllegalArgumentException(errorMessage);
            }

            log.info("Habitación creada exitosamente: " + response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error al crear la habitación", e);
        }
    }

    // Obtener reservas de un cliente (pistas y habitaciones)
    public List<CourtBookingResponse> getCourtBookingsByClienteId(Long clienteId) throws IOException, InterruptedException {
        log.info("Obteniendo reservas de pistas para clienteId: " + clienteId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/court-bookings/cliente/" + clienteId))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), new TypeReference<List<CourtBookingResponse>>() {});
        }

        throw new RuntimeException("Error al obtener las reservas del cliente: " + response.body());
    }

    public List<RoomBookingResponse> getRoomBookingsByClienteId(Long clienteId) throws IOException, InterruptedException {
        log.info("Obteniendo reservas de habitaciones para clienteId: " + clienteId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/room-bookings/cliente/" + clienteId))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), new TypeReference<List<RoomBookingResponse>>() {});
        }

        throw new RuntimeException("Error al obtener las reservas del cliente: " + response.body());
    }

    public void deleteCourtBooking(Long id)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:8080/api/v1/court-bookings/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Error al eliminar la reserva: " + response.body());
        }
    }

    public ResponseEntity<String> crearReserva(List<RoomBookingRequest> updatedRequests) throws IOException, InterruptedException {
        String jsonBody = objectMapper.writeValueAsString(updatedRequests);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:8080/api/v1/room-bookings"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();


        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return ResponseEntity.status(response.statusCode()).body(response.body());
    }
}
