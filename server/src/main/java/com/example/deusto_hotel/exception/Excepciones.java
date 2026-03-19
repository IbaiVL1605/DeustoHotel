package com.example.deusto_hotel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class Excepciones {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class UsuarioNoEncontradoException extends RuntimeException {
        public UsuarioNoEncontradoException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class UsuarioBloqueadoException extends RuntimeException {
        public UsuarioBloqueadoException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class CredencialesInvalidasException extends RuntimeException {
        public CredencialesInvalidasException(String message) {
            super(message);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class EmailYaRegistradoException extends RuntimeException {
        public EmailYaRegistradoException(String message) {
            super(message);
        }
    }
}