package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.UserRequest;
import com.example.deusto_hotel.dto.UserResponse;
import com.example.deusto_hotel.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST encargado de la gestión de usuarios.
 * <p>
 * Expone endpoints relacionados con el registro,
 * autenticación y gestión de usuarios del sistema.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    /**
     * Servicio encargado de la lógica de negocio
     * relacionada con usuarios.
     */
    private final UserService userService;

    /*
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        throw new UnsupportedOperationException();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        throw new UnsupportedOperationException();
    }
     */

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request datos del usuario a registrar
     * @return usuario creado con estado HTTP 201
     */
    @PostMapping
    public ResponseEntity<UserResponse> create(
            @RequestBody @Valid UserRequest request) {

        UserResponse response = userService.create(request);

        return ResponseEntity
                .status(201)
                .body(response);
    }

    /**
     * Inicia sesión de un usuario en el sistema.
     * <p>
     * Valida las credenciales recibidas y almacena
     * la información básica del usuario en la sesión HTTP.
     * </p>
     *
     * @param session sesión HTTP del usuario
     * @param correo correo electrónico del usuario
     * @param contrasena contraseña del usuario
     * @return mensaje de confirmación y datos del usuario autenticado
     * @throws IllegalArgumentException si el correo o la contraseña son inválidos
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            HttpSession session,
            @RequestParam String correo,
            @RequestParam String contrasena
    ) {

        if (correo == null || correo.isBlank()) {

            throw new IllegalArgumentException(
                    "El correo es obligatorio."
            );
        }

        if (contrasena == null || contrasena.isBlank()) {

            throw new IllegalArgumentException(
                    "La contrasena es obligatoria."
            );
        }

        UserResponse response =
                userService.login(correo, contrasena);

        // Guardar datos en sesión
        session.setAttribute("userId", response.id());
        session.setAttribute("username", response.nombre());
        session.setAttribute("userEmail", response.email());
        session.setAttribute("userRole", response.rol());

        return ResponseEntity.ok(
                Map.of(
                        "mensaje", "Sesion iniciada correctamente",
                        "usuario", response
                )
        );
    }

    /*
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid UserRequest request) {

        throw new UnsupportedOperationException();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        throw new UnsupportedOperationException();
    }
     */
}