package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.UserRequest;
import com.example.deusto_hotel.dto.UserResponse;
import com.example.deusto_hotel.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
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
@Tag(name = "Usuarios", description = "Endpoints para la gestión de usuarios del sistema")
public class UserController {

    /**
     * Servicio encargado de la lógica de negocio
     * relacionada con usuarios.
     */
    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

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
	@Operation(
			summary = "Registrar un nuevo usuario",
			description = "Crea un nuevo usuario en el sistema con los datos proporcionados. " +
					"Valida que el email sea único y que los datos cumplan con las restricciones de validación."
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "201",
					description = "Usuario registrado exitosamente",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = UserResponse.class)
					)
			),
			@ApiResponse(
					responseCode = "400",
					description = "Datos inválidos: email duplicado o validación fallida"
			),
			@ApiResponse(
					responseCode = "500",
					description = "Error interno del servidor"
			)
	})
	public ResponseEntity<UserResponse> create(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "Datos necesarios para registrar el usuario",
					required = true,
					content = @Content(schema = @Schema(implementation = UserRequest.class))
			)
			@RequestBody @Valid UserRequest request) {

		MDC.put("endpoint", "POST /api/v1/users");
		MDC.put("email", request.email());

		try {
			log.info("Solicitud de creación de nuevo usuario recibida");

			UserResponse response = userService.create(request);



			return ResponseEntity
					.status(201)
					.body(response);
		} finally {
			MDC.remove("endpoint");
			MDC.remove("email");
		}
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
	@Operation(
			summary = "Iniciar sesión de usuario",
			description = "Autentica un usuario validando sus credenciales (correo y contraseña). " +
					"Si la autenticación es exitosa, almacena la información en la sesión HTTP."
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "Sesión iniciada correctamente",
					content = @Content(mediaType = "application/json")
			),
			@ApiResponse(
					responseCode = "400",
					description = "Datos inválidos: correo o contraseña vacíos, o credenciales incorrectas"
			),
			@ApiResponse(
					responseCode = "500",
					description = "Error interno del servidor"
			)
	})
	public ResponseEntity<Map<String, Object>> login(
			HttpSession session,

			@Parameter(
					description = "Correo electrónico del usuario",
					required = true,
					example = "usuario@example.com"
			)
			@RequestParam String correo,

			@Parameter(
					description = "Contraseña del usuario",
					required = true,
					example = "password123"
			)
			@RequestParam String contrasena
	) {

		MDC.put("endpoint", "POST /api/v1/users/login");
		MDC.put("email", correo);
		MDC.put("sessionId", session.getId());

		try {
			log.info("Solicitud de login recibida");

			if (correo == null || correo.isBlank()) {
				log.warn("Validación fallida - Email es obligatorio");
				throw new IllegalArgumentException(
						"El correo es obligatorio."
				);
			}

			if (contrasena == null || contrasena.isBlank()) {
				log.warn("Validación fallida - Contraseña es obligatoria");
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

			MDC.put("userId", String.valueOf(response.id()));
			log.info("Sesión HTTP establecida para usuario");

			return ResponseEntity.ok(
					Map.of(
							"mensaje", "Sesion iniciada correctamente",
							"usuario", response
					)
			);
		} finally {
			MDC.remove("endpoint");
			MDC.remove("email");
			MDC.remove("sessionId");
			MDC.remove("userId");
		}
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