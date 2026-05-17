package com.example.deusto_hotel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de excepciones para toda la aplicación REST.
 * <p>
 * Esta clase actúa como un punto centralizado para capturar y procesar todas
 * las excepciones lanzadas en los controladores de la aplicación. Utiliza
 * la anotación {@link RestControllerAdvice} para traducir automáticamente
 * las excepciones en respuestas HTTP con códigos de estado y mensajes adecuados.
 * </p>
 * El manejador diferencia entre dos tipos de excepciones:
 * <ul>
 *   <li>{@link IllegalArgumentException}: Excepciones de validación/argumentos inválidos (HTTP 400)</li>
 *   <li>{@link RuntimeException}: Excepciones personalizadas con {@link ResponseStatus} (código específico)</li>
 * </ul>
 *
 * @author Deusto Hotel Team
 * @version 1.0
 * @see Excepciones
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de argumentos inválidos ({@link IllegalArgumentException}).
     * <p>
     * Captura todas las excepciones de tipo {@link IllegalArgumentException} lanzadas
     * en los controladores y las convierte en respuestas HTTP con código 400 (Bad Request).
     * Esta excepción se utiliza típicamente para validaciones de parámetros.
     * </p>
     *
     * @param e la excepción {@link IllegalArgumentException} capturada
     * @return {@link ResponseEntity} con código HTTP 400 (Bad Request) y el mensaje de error
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    /**
     * Maneja todas las demás excepciones de tiempo de ejecución ({@link RuntimeException}).
     * <p>
     * Captura las excepciones personalizadas de la aplicación (que extienden RuntimeException)
     * y busca la anotación {@link ResponseStatus} para determinar el código HTTP apropiado.
     * Si la excepción tiene {@link ResponseStatus}, utiliza su código. Si no, retorna 400 (Bad Request).
     * </p>
     * Este método es especialmente útil para procesar las excepciones personalizadas definidas
     * en {@link Excepciones}, que tienen anotaciones {@link ResponseStatus} con códigos HTTP específicos:
     * <ul>
     *   <li>UsuarioNoEncontradoException → 404 (Not Found)</li>
     *   <li>UsuarioBloqueadoException → 403 (Forbidden)</li>
     *   <li>CredencialesInvalidasException → 401 (Unauthorized)</li>
     *   <li>EmailYaRegistradoException → 409 (Conflict)</li>
     * </ul>
     *
     * @param e la excepción {@link RuntimeException} capturada
     * @return {@link ResponseEntity} con el código HTTP especificado en {@link ResponseStatus}
     *         (o 400 si no está especificado) y el mensaje de error
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        ResponseStatus responseStatus = e.getClass().getAnnotation(ResponseStatus.class);
        if (responseStatus != null) {
            return ResponseEntity.status(responseStatus.value()).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}

