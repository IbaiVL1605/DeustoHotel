package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.*;
import com.example.deusto_hotel.model.Role;
import com.example.deusto_hotel.proxy.Proxy;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
public class Controller {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    private final Proxy proxy;

    @GetMapping("/habitaciones/disponibles")
    public String getHabitacionesDisponibles(Model model, LocalDate fechaEntrada, LocalDate fechaSalida) throws IOException, InterruptedException, JsonProcessingException {

        if (fechaEntrada == null || fechaSalida == null) {
            return "user/habitaciones";
        }

        ArrayList<RoomDisponibleResponse> habitaciones = proxy.getHabitacionesDisponibles(fechaEntrada, fechaSalida);

        System.out.printf(habitaciones.toString());

        habitaciones.forEach(habitacion -> {
            String key = switch (habitacion.getTipo()) {
                case INDIVIDUAL -> "habitacionSimple";
                case DOBLE -> "habitacionDoble";
                case SUITE -> "habitacionSuite";
            };
            model.addAttribute(key, habitacion);
        });
        return "user/habitaciones";

    }

    @PostMapping("/reservas")
    public ResponseEntity<String> crearReserva(HttpSession sesion, @RequestBody List<RoomBookingRequest> request) throws IOException, InterruptedException {
        if(sesion.getAttribute("userId") == null){ return ResponseEntity.badRequest().body("Usuario no logeado"); }

        List<RoomBookingRequest> updatedRequests = request.stream()
                .map(r -> new RoomBookingRequest(r.tipo(), (Long) sesion.getAttribute("userId"), r.cantidad(), r.id_habitacion(), r.fechaEntrada(), r.fechaSalida()))
                .toList();


        return proxy.crearReserva(updatedRequests);
    }

    @GetMapping("/pistas")
    public String getPistas(
            Model model,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) throws IOException, InterruptedException, JsonProcessingException {
        LocalDate today = LocalDate.now();
        int currentYear = (year != null) ? year : today.getYear();
        int currentMonth = (month != null) ? month : today.getMonthValue();

        List<WeekAvailability> disponibilidad = proxy.getCourtsWeeklyAvailability(currentYear, currentMonth, tipo);

        model.addAttribute("disponibilidad", disponibilidad);
        model.addAttribute("tipo", tipo);
        model.addAttribute("year", currentYear);
        model.addAttribute("month", currentMonth);
        model.addAttribute("hoy", today);
        model.addAttribute("objectMapper", new ObjectMapper());
        return "user/pistas";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @GetMapping("/api/v1/login")
    public String login(HttpSession session, String email, String password)
            throws IOException, InterruptedException {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        UserResponse usuario = proxy.login(email, password);

        session.setAttribute("userId", usuario.id());

        session.setAttribute("username", usuario.nombre());
        session.setAttribute("userEmail", usuario.email());
        session.setAttribute("userRole", usuario.rol().name());


        if (usuario.rol() == Role.ADMIN) {
            return "redirect:/admin";
        } else {
            return "redirect:/habitaciones/disponibles";
        }
    }

    @GetMapping("/admin")
    public String adminPage(HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        System.out.println("ROL EN SESIÓN EN /admin: " + role);
        if (role == null || !role.equals("ADMIN")) {
            return "redirect:/login";
        }
        return "admin/admin";
    }
/*
    @GetMapping("/reservas/nueva")
    public String showCreateForm(Model model) {
        model.addAttribute("booking", new RoomBookingRequest(null, null, null, null));
        return "user/reserva-form";
    }
    */
/*
    @PostMapping("/reservas")
    @ResponseBody
    public String createBookings(@RequestBody List<RoomBookingRequest> requests,
                                 HttpSession session) {

        Long clienteId = (Long) session.getAttribute("userId");

        if (clienteId == null) {
            return "ERROR: Usuario no autenticado";
        }

        try {
            List<RoomBookingRequest> requestsConCliente = requests.stream()
                    .map(r -> new RoomBookingRequest(
                            r.habitacionId(),
                            r.checkIn(),
                            r.checkOut(),
                            clienteId
                    ))
                    .toList();

            proxy.createBookings(requestsConCliente);

            return "OK";

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
*/
    /*
    @GetMapping("/reservas/editar/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("bookingId", id);
        model.addAttribute("booking", new RoomBookingRequest(null, null, null, null));
        return "user/reserva-form";
    }

    @PostMapping("/reservas/{id}")
    public String updateBooking(@PathVariable Long id, RoomBookingRequest request, Model model) {
        try {
            proxy.updateRoomBooking(id, request);
            return "redirect:/habitaciones/disponibles";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "user/reserva-form";
        }
    }
*/
@PostMapping("/reservas/eliminar/{id}")
public String deleteBooking(@PathVariable Long id, HttpSession session) {

    Long userId = (Long) session.getAttribute("userId");

    if (userId == null) {
        return "redirect:/login";
    }

    try {
        proxy.deleteRoomBooking(id, userId);
    } catch (Exception e) {
        System.out.println("Error al eliminar: " + e.getMessage());
    }

    return "redirect:/reservas";
}

    /*
 @PostMapping("/admin/rooms")
 public String crearHabitacionFromForm(@ModelAttribute RoomRequest request, Model model) {
     try {
         proxy.crearHabitacion(request);
         model.addAttribute("success", "Habitación creada correctamente");
     } catch (Exception e) {
         model.addAttribute("error", e.getMessage());
     }
     return "/admin";
 }
 */

    @PostMapping("/api/v1/court-bookings")
    @ResponseBody
    public String createCourtBooking(@RequestBody com.example.deusto_hotel.dto.CourtBookingRequest request, HttpSession session) {
        Long clienteId = (Long) session.getAttribute("userId");
        if (clienteId == null) {
            return "{\"status\":\"ERROR\", \"message\":\"Usuario no autenticado\"}";
        }
        try {
            com.example.deusto_hotel.dto.CourtBookingRequest requestConCliente = new com.example.deusto_hotel.dto.CourtBookingRequest(
                    request.pistaId(),
                    request.fecha(),
                    request.horaInicio(),
                    request.horaFin(),
                    clienteId
            );
            proxy.createCourtBooking(requestConCliente);
            return "{\"status\":\"OK\"}";
        } catch (Exception e) {
            return "{\"status\":\"ERROR\", \"message\":\"" + e.getMessage() + "\"}";
        }
    }

    @GetMapping("/menu")
    public String showMenu(HttpSession session, Model model) {

        String role = (String) session.getAttribute("userRole");

        if (role == null) {
            return "redirect:/login";
        }

        model.addAttribute("role", role);

        return "auth/menu"; // ruta del HTML
    }

    @PostMapping("/admin")
    public String crearHabitacion(@ModelAttribute RoomRequest request, Model model) {
        System.out.println("REQUEST RECIBIDA: " + request);

        try {
            proxy.crearHabitacion(request);
            model.addAttribute("success", "Habitación creada correctamente");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "admin/admin";
    }

    // Menu para ver reservas (tanto de habitaciones como de pistas)
    @GetMapping("/reservas")
    public String verReservas(HttpSession session, Model model) {
        long clienteId = (long) session.getAttribute("userId");
        if (clienteId == 0) {
            return "redirect:/login";
        }

        try {
            log.info("Obteniendo reservas para clienteId: " + clienteId);

            List<RoomBookingResponse> roomBookings = proxy.getRoomBookingsByClienteId(clienteId);
            List<CourtBookingResponse> courtBookings = proxy.getCourtBookingsByClienteId(clienteId);

            model.addAttribute("roomBookings", roomBookings);
            model.addAttribute("courtBookings", courtBookings);

            return "user/reservas";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "user/reservas";
        }
    }

    @PostMapping("/reservas/eliminar/pista/{id}")
    public String deleteCourtBooking(@PathVariable Long id) {
        try {
            proxy.deleteCourtBooking(id);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return "redirect:/reservas";
    }


}
