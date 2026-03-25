package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.RoomDisponibleResponse;
import com.example.deusto_hotel.dto.UserResponse;
import com.example.deusto_hotel.dto.WeekAvailability;
import com.example.deusto_hotel.model.Role;
import com.example.deusto_hotel.proxy.Proxy;
import jakarta.servlet.http.HttpSession;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor    
public class Controller {

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



            return "redirect:/menu";

    }

    @GetMapping("/signup")
    public String showSignupForm() {
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String password,
            RedirectAttributes redirectAttributes
    ) {
        try {
            proxy.signup(nombre, email, password);
            redirectAttributes.addFlashAttribute("success", "Cuenta creada correctamente. ¡Ya puedes iniciar sesión!");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar: " + e.getMessage());
            return "redirect:/signup";
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
    @GetMapping("/menu")
    public String showMenu(HttpSession session, Model model) {

        String role = (String) session.getAttribute("userRole");

        if (role == null) {
            return "redirect:/login";
        }

        model.addAttribute("role", role);

        return "auth/menu"; // ruta del HTML
    }


}