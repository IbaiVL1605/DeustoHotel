package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.UserRequest;
import com.example.deusto_hotel.dto.UserResponse;
import com.example.deusto_hotel.exception.Excepciones;
import com.example.deusto_hotel.mapper.UserMapper;
import com.example.deusto_hotel.model.Role;
import com.example.deusto_hotel.model.User;
import com.example.deusto_hotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para la gestión de usuarios del hotel.
 *
 * Proporciona funcionalidades para crear nuevos usuarios, autenticar usuarios existentes
 * y validar credenciales. Maneja la persistencia de datos de usuarios mediante el
 * repositorio y utiliza mappers para convertir entre DTOs y entidades.
 *
 * @author Deusto Hotel Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    /**
     * Crea un nuevo usuario en el sistema.
     *
     * Valida que no exista un usuario con el correo proporcionado. Si la validación falla,
     * se lanzará una {@link Excepciones.EmailYaRegistradoException}. El nuevo usuario se
     * registra automáticamente con el rol CLIENT y estado desbloqueado.
     *
     * @param request Objeto {@link UserRequest} con los datos del usuario a crear.
     *                Contiene los campos: nombre, email y password.
     *                Debe cumplir con las validaciones definidas en UserRequest.
     *
     * @return {@link UserResponse} con los datos del usuario creado,
     *         incluyendo su ID asignado automáticamente, rol (CLIENT),
     *         estado de bloqueo (false) y fecha de creación.
     *
     * @throws Excepciones.EmailYaRegistradoException Si ya existe un usuario con el correo proporcionado.
     */
    public UserResponse create(UserRequest request) {
        MDC.put("operation", "createUser");
        MDC.put("email", request.email());

        try {
            log.info("Intento de creación de nuevo usuario");

            if(userRepository.existsByEmail(request.email())) {
                log.warn("Intento de creación fallido - Email ya registrado");
                throw new Excepciones.EmailYaRegistradoException("El correo ya está registrado");
            }

            User usuario = userMapper.toEntity(request);

            usuario.setRol(Role.CLIENT);
            usuario.setBloqueado(false);

            usuario = userRepository.save(usuario);

            MDC.put("userId", String.valueOf(usuario.getId()));
            log.info("Usuario creado exitosamente. Rol: {}", usuario.getRol());

            return userMapper.toResponse(usuario);
        } finally {
            MDC.remove("operation");
            MDC.remove("email");
            MDC.remove("userId");
        }
    }

    /**
     * Autentica un usuario existente en el sistema.
     *
     * Valida que el usuario exista con el correo proporcionado y que la contraseña sea correcta.
     * Además, verifica que el usuario no esté bloqueado. Si alguna de estas validaciones falla,
     * se lanzará la excepción correspondiente.
     *
     * Validaciones:
     * - El usuario debe existir en el sistema.
     * - La contraseña debe coincidir con la almacenada.
     * - El usuario no puede estar bloqueado.
     *
     * @param correo Correo electrónico del usuario a autenticar.
     *               Debe corresponder a un usuario existente en el sistema.
     *
     * @param contrasena Contraseña en texto plano del usuario.
     *                   Debe coincidir exactamente con la contraseña almacenada del usuario.
     *
     * @return {@link UserResponse} con los datos del usuario autenticado,
     *         incluyendo su ID, nombre, correo, rol, estado de bloqueo y fecha de creación.
     *
     * @throws Excepciones.UsuarioNoEncontradoException Si no existe un usuario con el correo proporcionado.
     *
     * @throws Excepciones.CredencialesInvalidasException Si la contraseña proporcionada no coincide
     *                                                     con la contraseña almacenada del usuario.
     *
     * @throws Excepciones.UsuarioBloqueadoException Si el usuario existe pero está bloqueado en el sistema.
     */
    public UserResponse login(String correo, String contrasena) {

        MDC.put("operation", "login");
        MDC.put("email", correo);

        try {
            log.info("Intento de login para usuario");

            User usuario = userRepository.findByEmail(correo)
                    .orElseThrow(() -> {
                        log.warn("Intento de login fallido - Usuario no encontrado");
                        return new Excepciones.UsuarioNoEncontradoException("Usuario no encontrado");
                    });

            MDC.put("userId", String.valueOf(usuario.getId()));

            if (!usuario.getPassword().equals(contrasena)) {
                log.warn("Intento de login fallido - Contraseña incorrecta");
                throw new Excepciones.CredencialesInvalidasException("Contrasena incorrecta");
            }

            if (usuario.isBloqueado()) {
                log.warn("Intento de login fallido - Usuario bloqueado");
                throw new Excepciones.UsuarioBloqueadoException("Usuario bloqueado");
            }

            log.info("Login exitoso");

            return new UserResponse(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getEmail(),
                    usuario.getRol(),
                    usuario.isBloqueado(),
                    usuario.getCreadoEn()
            );
        } finally {
            MDC.remove("operation");
            MDC.remove("email");
            MDC.remove("userId");
        }
    }
}
