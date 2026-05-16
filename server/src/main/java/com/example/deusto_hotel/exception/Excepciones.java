package com.example.deusto_hotel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Contenedor de excepciones personalizadas para el sistema de gestión del hotel.
 * <p>
 * Esta clase agrupa todas las excepciones personalizadas del dominio de usuarios,
 * cada una asignada a un código de estado HTTP específico que será retornado
 * en las respuestas de error. Las excepciones están anotadas con {@link ResponseStatus}
 * para permitir que Spring las traduzca automáticamente a respuestas HTTP.
 * </p>
 *
 * @author Deusto Hotel Team
 * @version 1.0
 */
public class Excepciones {

    /**
     * Excepción lanzada cuando se intenta acceder a un usuario que no existe.
     * <p>
     * Esta excepción se lanza típicamente durante operaciones de búsqueda
     * (por ejemplo, durante el login) cuando no se encuentra un usuario con
     * el correo proporcionado.
     * </p>
     * Código HTTP: 404 (Not Found)
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class UsuarioNoEncontradoException extends RuntimeException {
        /**
         * Construye una excepción de usuario no encontrado.
         *
         * @param message mensaje descriptivo del error
         */
        public UsuarioNoEncontradoException(String message) {
            super(message);
        }
    }

    /**
     * Excepción lanzada cuando se intenta acceder a una cuenta de usuario bloqueada.
     * <p>
     * Esta excepción se lanza durante el login cuando el usuario existe y
     * las credenciales son válidas, pero su cuenta está bloqueada en el sistema.
     * Los usuarios bloqueados no pueden realizar reservas ni otras operaciones.
     * </p>
     * Código HTTP: 403 (Forbidden)
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class UsuarioBloqueadoException extends RuntimeException {
        /**
         * Construye una excepción de usuario bloqueado.
         *
         * @param message mensaje descriptivo del error
         */
        public UsuarioBloqueadoException(String message) {
            super(message);
        }
    }

    /**
     * Excepción lanzada cuando las credenciales de acceso son inválidas.
     * <p>
     * Esta excepción se lanza durante el login cuando la contraseña proporcionada
     * no coincide con la contraseña almacenada del usuario encontrado.
     * </p>
     * Código HTTP: 401 (Unauthorized)
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class CredencialesInvalidasException extends RuntimeException {
        /**
         * Construye una excepción de credenciales inválidas.
         *
         * @param message mensaje descriptivo del error
         */
        public CredencialesInvalidasException(String message) {
            super(message);
        }
    }

    /**
     * Excepción lanzada cuando se intenta registrar un usuario con un correo ya registrado.
     * <p>
     * Esta excepción se lanza durante la creación de un nuevo usuario cuando ya existe
     * otro usuario en el sistema con el mismo correo electrónico. Los correos electrónicos
     * deben ser únicos.
     * </p>
     * Código HTTP: 409 (Conflict)
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    public static class EmailYaRegistradoException extends RuntimeException {
        /**
         * Construye una excepción de correo ya registrado.
         *
         * @param message mensaje descriptivo del error
         */
        public EmailYaRegistradoException(String message) {
            super(message);
        }
    }
}