package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.UserRequest;
import com.example.deusto_hotel.dto.UserResponse;
import com.example.deusto_hotel.exception.Excepciones;
import com.example.deusto_hotel.mapper.UserMapper;
import com.example.deusto_hotel.model.Role;
import com.example.deusto_hotel.model.User;
import com.example.deusto_hotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        // Usar Mapper para convertir UserRequest a User, guardar en la base de datos y luego convertir a UserResponse
        if(userRepository.existsByEmail(request.email())) {
            throw new Excepciones.EmailYaRegistradoException("El correo ya está registrado");
        }

        User usuario = userMapper.toEntity(request);

        usuario.setRol(Role.CLIENT);   // o USER según tu enum
        usuario.setBloqueado(false);

        usuario = userRepository.save(usuario);

        return userMapper.toResponse(usuario);
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

        User usuario = userRepository.findByEmail(correo)
                .orElseThrow(() -> new Excepciones.UsuarioNoEncontradoException("Usuario no encontrado"));

        if (!usuario.getPassword().equals(contrasena)) {
            throw new Excepciones.CredencialesInvalidasException("Contrasena incorrecta");
        }

        if (usuario.isBloqueado()) {
            throw new Excepciones.UsuarioBloqueadoException("Usuario bloqueado");
        }

        return new UserResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.isBloqueado(),
                usuario.getCreadoEn()
        );
    }
}
