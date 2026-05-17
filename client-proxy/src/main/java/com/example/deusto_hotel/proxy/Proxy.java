package com.example.deusto_hotel.proxy;

import com.example.deusto_hotel.dto.*;
import com.example.deusto_hotel.model.RoomType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class Proxy {

    private static final Logger log = LoggerFactory.getLogger(Proxy.class);
    private final HttpClient httpClient;

    public Proxy(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private ArrayList<RoomDisponibleResponse> parseRoomDisponibleResponse(String response)
            throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(response);
        ArrayList<RoomDisponibleResponse> roomDisponibleResponses = new ArrayList<>();

        node.forEach(item -> {
            if (item.get("tipo").asText().equals(RoomType.SUITE.toString())) {

                JsonNode suitesNode = item.get("suites");

                List<SuitResponse> suites = StreamSupport
                        .stream(suitesNode.spliterator(), false) // ArrayNode → Stream
                        .map(suit -> new SuitResponse(
                                suit.get("capacidad").asInt(),
                                suit.get("precioPorNoche").asInt(),
                                suit.get("id").asInt()))
                        .collect(Collectors.toList());

                roomDisponibleResponses.add(new RoomDisponiblesSuitResponse(
                        RoomType.SUITE,
                        suites));
            } else {
                roomDisponibleResponses.add(new RoomDisponiblesSimplesResponse(
                        RoomType.valueOf(item.get("tipo").asText()),
                        item.get("numero_disponibles").asInt()));
            }
        });

        return roomDisponibleResponses;
    }

    public ArrayList<RoomDisponibleResponse> getHabitacionesDisponibles(LocalDate fechaEntrada, LocalDate fechaSalida)
            throws IOException, InterruptedException, JsonProcessingException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(
                        String.format("http://localhost:8080/api/v1/rooms/disponibles?fechaEntrada=%s&fechaSalida=%s",
                                fechaEntrada.toString(), fechaSalida.toString())))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if ((response.statusCode() != 200)) {
            throw new RuntimeException("Error al obtener habitaciones disponibles: " + response.body());
        }

        return parseRoomDisponibleResponse(response.body());
    }

    public List<CourtResponse> getCourts(String tipo)
            throws IOException, InterruptedException, JsonProcessingException {
        String url = "http://localhost:8080/api/v1/courts";
        if (tipo != null && !tipo.trim().isEmpty()) {
            url += "?tipo=" + tipo;
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<List<CourtResponse>>() {
        });
    }

    public List<WeekAvailability> getCourtsWeeklyAvailability(int year, int month, String tipo)
            throws IOException, InterruptedException, JsonProcessingException {
        StringBuilder url = new StringBuilder(
                String.format("http://localhost:8080/api/v1/courts/weekly-availability?year=%d&month=%d", year, month));
        if (tipo != null && !tipo.trim().isEmpty()) {
            url.append("&tipo=").append(tipo);
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(url.toString()))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<List<WeekAvailability>>() {
        });
    }

    public List<?> getCourtsAvailable(String tipo, String fecha, Integer semana)
            throws IOException, InterruptedException, JsonProcessingException {
        StringBuilder url = new StringBuilder("http://localhost:8080/api/v1/courts/available?");
        if (tipo != null && !tipo.trim().isEmpty())
            url.append("tipo=").append(tipo).append("&");
        if (fecha != null && !fecha.trim().isEmpty())
            url.append("fecha=").append(fecha).append("&");
        if (semana != null)
            url.append("semana=").append(semana);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(url.toString()))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), new TypeReference<List<?>>() {
        });
    }

    public UserResponse login(String email, String password) throws IOException, InterruptedException {
        String correo = URLEncoder.encode(email, StandardCharsets.UTF_8);
        String contrasena = URLEncoder.encode(password, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(String.format(
                        "http://localhost:8080/api/v1/users/login?correo=%s&contrasena=%s",
                        correo,
                        contrasena)))
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
            UserRequest user = new UserRequest(nombre, email, password);

            String json = objectMapper.writeValueAsString(user);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/users"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
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
    public List<CourtBookingResponse> getCourtBookingsByClienteId(Long clienteId)
            throws IOException, InterruptedException {
        log.info("Obteniendo reservas de pistas para clienteId: " + clienteId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/court-bookings/cliente/" + clienteId))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), new TypeReference<List<CourtBookingResponse>>() {
            });
        }

        throw new RuntimeException("Error al obtener las reservas del cliente: " + response.body());
    }

    public List<RoomBookingResponse> getRoomBookingsByClienteId(Long clienteId)
            throws IOException, InterruptedException {
        log.info("Obteniendo reservas de habitaciones para clienteId: " + clienteId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/room-bookings/cliente/" + clienteId))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), new TypeReference<List<RoomBookingResponse>>() {
            });
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

    public ResponseEntity<String> crearReserva(List<RoomBookingRequest> updatedRequests)
            throws IOException, InterruptedException {
        String jsonBody = objectMapper.writeValueAsString(updatedRequests);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:8080/api/v1/room-bookings"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return ResponseEntity.status(response.statusCode()).body(response.body());
    }

    public CourtBookingResponse updateCourtBooking(Long id, CourtBookingRequest request)
            throws IOException, InterruptedException {

        String jsonBody = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/court-bookings/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), CourtBookingResponse.class);
        }

        throw new RuntimeException("Error actualizando la reserva: " + response.body());
    }

    public void blockCourt(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/courts/" + id + "/block"))
                .POST(HttpRequest.BodyPublishers.noBody()) // No necesitamos enviar JSON, solo la URL con el ID
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Error al bloquear la pista: " + response.body());
        }
    }

    public void unblockCourt(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/courts/" + id + "/unblock"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Error al desbloquear la pista: " + response.body());
        }
    }

    public void bloquearHabitacion(Long id)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/rooms/" + id + "/bloquear"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException(
                    "Error al bloquear habitación: " + response.body());
        }
    }

    public List<RoomBookingResponse> getAllRoomBookings()
            throws IOException, InterruptedException {

        log.info("Obteniendo todas las reservas de habitaciones");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:8080/api/v1/room-bookings"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error obteniendo reservas de habitaciones");
        }

        log.info("Respuesta del servidor: " + response.body());

        return objectMapper.readValue(
                response.body(),
                new TypeReference<List<RoomBookingResponse>>() {
                });
    }

    public List<CourtBookingResponse> getAllCourtBookings()
            throws IOException, InterruptedException {
        log.info("Obteniendo todas las reservas de pistas");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:8080/api/v1/court-bookings"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error obteniendo reservas de pistas");
        }

        log.info("Respuesta del servidor: " + response.body());

        return objectMapper.readValue(
                response.body(),
                new TypeReference<List<CourtBookingResponse>>() {
                });
    }

    // validar reserva
    public ResponseEntity<String> validarReserva(Long idReserva, Long idRecepcionista) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:8080/api/v1/room-bookings/validar?idReserva="
                        + idReserva + "&idRecepcionista=" + idRecepcionista))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(response.statusCode()).body(response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error al validar la reserva", e);
        }
    }

    public CourtBookingResponse getCourtBookingById(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create("http://localhost:8080/api/v1/court-bookings/" + id))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            log.info("Reserva de pista encontrada: " + response.body());
            return objectMapper.readValue(response.body(), CourtBookingResponse.class);
        } else {
            log.info("No se encontró la reserva de pista con ID: " + id);
            throw new RuntimeException("Error al obtener la reserva de pista: " + response.body());
        }
    }

    public List<LocalTime> getHorasDisponibles(Long pistaId, LocalDate fecha) {
        try {
            int year = fecha.getYear();
            int month = fecha.getMonthValue();

            String url = String.format(
                    "http://localhost:8080/api/v1/courts/weekly-availability?year=%d&month=%d",
                    year, month);

            System.out.println("====== [PROXY] URL LLAMADA ======");
            System.out.println(url);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println("====== [PROXY] RESPUESTA DEL SERVIDOR ======");
            System.out.println(response.body());

            JsonNode rootNode = objectMapper.readTree(response.body());
            List<LocalTime> horasDisponibles = new ArrayList<>();

            if (rootNode.isArray()) {
                for (JsonNode week : rootNode) {
                    JsonNode daysNode = week.get("days");
                    if (daysNode != null && daysNode.isArray()) {
                        for (JsonNode day : daysNode) {
                            String dateStr = day.get("date").asText();

                            if (dateStr.equals(fecha.toString())) {
                                System.out.println("-> [PROXY] ¡Día encontrado!: " + dateStr);

                                JsonNode slotsNode = day.get("slots");
                                if (slotsNode != null && slotsNode.isArray()) {

                                    for (JsonNode slot : slotsNode) {
                                        // Cambiado a "availableCourts" que es el nombre real en tu JSON
                                        JsonNode courtsNode = slot.get("availableCourts");

                                        if (courtsNode != null && courtsNode.isArray()) {
                                            for (JsonNode court : courtsNode) {

                                                if (court.get("id").asLong() == pistaId) {
                                                    // Cambiado a "start" que es el nombre real en tu JSON (ej:
                                                    // "08:00:00")
                                                    String startTimeStr = slot.get("start").asText();
                                                    LocalTime hora = LocalTime.parse(startTimeStr.substring(0, 5));
                                                    horasDisponibles.add(hora);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            System.out.println("====== [PROXY] HORAS FINALES EXTRAÍDAS ======");
            System.out.println(horasDisponibles);
            return horasDisponibles;

        } catch (Exception e) {
            log.error("Excepción al procesar disponibilidad: ", e);
            return List.of();
        }
    }

    public void cancelCourtBookingAdmin(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/v1/court-bookings/" + id + "/cancel"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Error al cancelar la reserva por el administrador: " + response.body());
        }
    }
}
