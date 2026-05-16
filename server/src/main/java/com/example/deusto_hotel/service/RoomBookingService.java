package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.dto.RoomBookingResponse;
import com.example.deusto_hotel.exception.Excepciones;
import com.example.deusto_hotel.mapper.RoomBookingMapper;
import com.example.deusto_hotel.model.*;
import com.example.deusto_hotel.repository.RoomBookingRepository;
import com.example.deusto_hotel.repository.UserRepository;
import com.example.deusto_hotel.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de reservas de habitaciones del hotel.
 *
 * Proporciona métodos para crear, eliminar y consultar reservas de habitaciones.
 * Gestiona la lógica de negocio para validar fechas, disponibilidad de habitaciones,
 * y la existencia de clientes antes de procesar las reservas.
 *
 * @author Deusto Hotel Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RoomBookingService {

    private final RoomBookingRepository roomBookingRepository;
    private final RoomBookingMapper roomBookingMapper;
    private final RoomRepository roomRepository;
    private  final UserRepository userRepository;

    /**
     * Crea una lista de reservas de habitaciones para uno o más clientes.
     *
     * Valida las fechas, el tipo de habitación y la disponibilidad antes de guardar las reservas.
     * Para las habitaciones tipo SUITE, se reserva la habitación específica solicitada.
     * Para las habitaciones tipo INDIVIDUAL o DOBLE, se asignan automáticamente habitaciones
     * disponibles del tipo solicitado.
     *
     * Validaciones realizadas:
     * - La fecha de salida debe ser posterior a la fecha de entrada.
     * - La fecha de entrada no puede ser anterior a la fecha actual.
     * - El tipo de habitación debe ser válido (SUITE, INDIVIDUAL o DOBLE).
     * - El cliente debe existir en el sistema.
     * - Debe haber habitaciones disponibles del tipo solicitado para las fechas indicadas.
     * - Para SUITE, no debe haber solapamiento con otras reservas en la misma habitación.
     * - Para INDIVIDUAL/DOBLE, debe haber suficientes habitaciones disponibles según la cantidad solicitada.
     *
     * @param request Lista de objetos {@link RoomBookingRequest} con los datos de las reservas a crear.
     *                Cada solicitud contiene: id_cliente, id_habitacion, tipo, fechaEntrada, fechaSalida y cantidad.
     *
     * @throws IllegalArgumentException Si:
     *         - Las fechas no cumplen con las validaciones (salida no posterior a entrada,
     *           o entrada anterior a la fecha actual).
     *         - El tipo de habitación es nulo o inválido.
     *         - El cliente (id_cliente) no existe en el sistema.
     *         - Para SUITE: No existe habitación con el ID y tipo SUITE especificados,
     *           o hay solapamiento con otras reservas en las fechas indicadas.
     *         - Para INDIVIDUAL/DOBLE: No hay suficientes habitaciones disponibles del tipo
     *           y cantidad solicitadas para las fechas indicadas.
     */
    @Transactional()
    public void create(List<RoomBookingRequest> request) {

        for  (RoomBookingRequest roomBookingRequest : request) {
            validarFechas(roomBookingRequest);

            if (roomBookingRequest.tipo() == null) {
                throw new IllegalArgumentException("Tipo de habitación no válido");
            }

            User cliente = userRepository.findById(roomBookingRequest.id_cliente())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

            switch (roomBookingRequest.tipo()) {
                case INDIVIDUAL, DOBLE -> reservarSimples(roomBookingRequest, cliente);
                case SUITE -> reservarSuits(roomBookingRequest, cliente);
            }
        }


    }

    /**
     * Realiza la reserva de una habitación tipo SUITE.
     *
     * Valida que la habitación SUITE exista y no tenga solapamientos de reservas
     * en el período especificado. Crea la reserva con el precio total basado en
     * el precio por noche de la habitación.
     *
     * @param roomBookingRequest Datos de la solicitud de reserva con ID de habitación y fechas.
     * @param cliente Usuario cliente que realiza la reserva.
     *
     * @throws IllegalArgumentException Si no existe habitación con el ID especificado
     *                                   o si ya hay reservas solapadas para las fechas indicadas.
     */
    private void reservarSuits(RoomBookingRequest roomBookingRequest, User cliente) {
        System.out.printf("Reserva SUITE: %s\n", roomBookingRequest.id_habitacion());
        Optional<Room> habitacion = roomRepository.findByIdAndTipo(roomBookingRequest.id_habitacion(), RoomType.SUITE);
        if(habitacion.isEmpty()) {throw new IllegalArgumentException("Habitacion no encontrada para tipo SUITE");}

        // Verificar solapamientos con otras reservas
        List<RoomBooking> solapamientos = roomBookingRepository.findSolapamientos(
                habitacion.get().getId(),
                roomBookingRequest.fechaEntrada(),
                roomBookingRequest.fechaSalida()
        );

        if (!solapamientos.isEmpty()) {
            throw new IllegalArgumentException("La habitación ya está reservada para las fechas seleccionadas");
        }

        RoomBooking reserva = new RoomBooking();
        reserva.setCliente(cliente);
        reserva.setHabitacion(habitacion.get());
        reserva.setCheckIn(roomBookingRequest.fechaEntrada());
        reserva.setCheckOut(roomBookingRequest.fechaSalida());
        reserva.setPrecioTotal(habitacion.get().getPrecioPorNoche());

        roomBookingRepository.save(reserva);
    }

    /**
     * Valida las fechas de entrada y salida de una solicitud de reserva.
     *
     * Realiza las siguientes validaciones:
     * - La fecha de salida debe ser posterior a la fecha de entrada.
     * - La fecha de entrada no puede ser anterior a la fecha actual.
     *
     * @param roomBookingRequest Solicitud de reserva con las fechas a validar.
     *
     * @throws IllegalArgumentException Si la fecha de salida no es posterior a la entrada,
     *                                   o si la fecha de entrada es anterior a la fecha actual.
     */
    private void validarFechas(RoomBookingRequest roomBookingRequest) {
        if(!roomBookingRequest.fechaSalida().isAfter(roomBookingRequest.fechaEntrada())) {
            throw new IllegalArgumentException("La fecha de salida debe de ser posterior a la fecha de entrada.");

        } if(roomBookingRequest.fechaEntrada().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de entrada no puede ser anterior a la fecha actual.");

        }

    }

    /**
     * Realiza la reserva de una o más habitaciones tipo INDIVIDUAL o DOBLE.
     *
     * Busca habitaciones disponibles del tipo solicitado en el período especificado.
     * Asigna automáticamente habitaciones disponibles (la cantidad solicitada) al cliente.
     * Cada habitación se reserva con el precio total basado en su precio por noche.
     *
     * @param roomBookingRequest Datos de la solicitud con tipo, fechas, cantidad y cliente.
     * @param cliente Usuario cliente que realiza la reserva.
     *
     * @throws IllegalArgumentException Si no hay habitaciones disponibles del tipo solicitado
     *                                   en la cantidad requerida para las fechas indicadas.
     */
    private void reservarSimples(RoomBookingRequest roomBookingRequest, User cliente) {

        List<Room> disponiblesTotal = roomRepository.findRoomDisponibles(roomBookingRequest.fechaEntrada(), roomBookingRequest.fechaSalida());
        if(disponiblesTotal.isEmpty()) {throw new IllegalArgumentException("No hay habitaciones disponibles para las fechas seleccionadas");}

        List<Room> disponibles = disponiblesTotal.stream()
                .filter(h -> h.getTipo().equals(roomBookingRequest.tipo()))
                .toList();


        if(disponibles.size() < roomBookingRequest.cantidad()) {throw new IllegalArgumentException(String.format("No hay habitaciones disponibles para las fechas seleccionadas con el tipo: %s", roomBookingRequest.tipo()));}

        for (int i = 0; i < roomBookingRequest.cantidad(); i++) {
            RoomBooking reserva = new RoomBooking();

            reserva.setCliente(cliente);
            reserva.setCheckIn(roomBookingRequest.fechaEntrada());
            reserva.setCheckOut(roomBookingRequest.fechaSalida());


            Room habitacion = disponibles.get(i);
            reserva.setPrecioTotal(habitacion.getPrecioPorNoche());
            reserva.setHabitacion(habitacion);


            roomBookingRepository.save(reserva);
        }



    }

    public void validarReserva(Long idReserva, Long idRecepcionista) {
        if (idRecepcionista == null) {
            throw new IllegalArgumentException("Usuario no autenticado");
        }

        User recepcionista = userRepository.findById(idRecepcionista)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuario no encontrado"));

        if (recepcionista.getRol() != Role.RECEPTIONIST) {
            throw new IllegalArgumentException("Usuario no autorizado");
        }

        RoomBooking reserva = roomBookingRepository.findById(idReserva)
                .orElseThrow(() ->
                        new IllegalArgumentException("Reserva no encontrada"));

        if (reserva.getEstado() != RoomBookingStatus.PENDIENTE) {
            throw new IllegalArgumentException("La reserva no está en estado pendiente");
        }

        reserva.setEstado(RoomBookingStatus.CONFIRMADA);

        roomBookingRepository.save(reserva);
    }

    /**
     * Elimina una reserva específica, verificando que el usuario sea el propietario.
     *
     * Realiza validaciones de seguridad:
     * - Verifica que la reserva exista en el sistema.
     * - Verifica que el usuario esté autenticado (userId no nulo).
     * - Verifica que el usuario sea el propietario de la reserva (cliente).
     *
     * Solo el cliente que realizó la reserva puede cancelarla. Los administradores
     * u otros usuarios no pueden cancelar reservas ajenas.
     *
     * @param id Identificador único de la reserva a eliminar.
     * @param userId Identificador del usuario que realiza la eliminación (debe ser el propietario).
     *
     * @throws ResponseStatusException con código 404 (Not Found) si la reserva no existe.
     * @throws ResponseStatusException con código 401 (Unauthorized) si userId es nulo
     *                                   (usuario no autenticado).
     * @throws ResponseStatusException con código 403 (Forbidden) si el userId no coincide
     *                                   con el ID del cliente propietario de la reserva.
     */
    public void delete(Long id, Long userId) {

        RoomBooking booking = roomBookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        if (!booking.getCliente().getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "No puedes cancelar esta reserva");
        }

        roomBookingRepository.deleteById(id);
     }

    /**
     * Obtiene todas las reservas asociadas a un cliente específico.
     *
     * Recupera todas las reservas del cliente ordenadas por fecha de creación
     * o según la lógica del repositorio. Las reservas se mapean a objetos
     * {@link RoomBookingResponse} para ser retornadas.
     *
     * Este método es de solo lectura (readOnly = true) para optimizar
     * el rendimiento en consultas sin necesidad de transacción completa.
     *
     * @param clienteId Identificador único del cliente cuyas reservas se desean obtener.
     *
     * @return Lista de objetos {@link RoomBookingResponse} con todas las reservas del cliente.
     *         Puede retornar una lista vacía si el cliente no tiene reservas.
     */
    @Transactional(readOnly = true)
    public List<RoomBookingResponse> findByClienteId(Long clienteId) {
        return roomBookingRepository.findByClienteId(clienteId)
                .stream()
                .map(roomBookingMapper::toResponse)
                .toList();
    }



    public List<RoomBookingResponse> findAll() {
        return roomBookingRepository.findAll()
                .stream()
                .map(roomBookingMapper::toResponse)
                .toList();
    }

}
