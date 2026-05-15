package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.RoomDisponibleResponse;
import com.example.deusto_hotel.dto.RoomRequest;
import com.example.deusto_hotel.dto.RoomResponse;
import com.example.deusto_hotel.mapper.RoomMapper;
import com.example.deusto_hotel.model.Room;
import com.example.deusto_hotel.model.RoomType;
import com.example.deusto_hotel.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para la gestión de habitaciones del hotel.
 *
 * Proporciona métodos para crear, eliminar y consultar habitaciones,
 * así como para buscar habitaciones disponibles en un rango de fechas específico.
 * Gestiona la lógica de negocio para validar data de habitación y disponibilidad.
 *
 * @author Deusto Hotel Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    /**
     * Obtiene la lista de habitaciones disponibles en un rango de fechas específico.
     *
     * Busca todas las habitaciones que no tienen reservas que se solapen con el
     * rango de fechas proporcionado (desde fechaEntrada hasta fechaSalida).
     * Las habitaciones se retornan mapeadas a objetos {@link RoomDisponibleResponse}.
     *
     * Este método es de solo lectura (readOnly = true) para optimizar el rendimiento
     * en consultas sin necesidad de transacción completa.
     *
     * @param fechaEntrada Fecha de entrada solicitada (inclusive). Formato: yyyy-MM-dd.
     *
     * @param fechaSalida Fecha de salida solicitada (inclusive). Formato: yyyy-MM-dd.
     *
     * @return Lista de objetos {@link RoomDisponibleResponse} con las habitaciones disponibles
     *         en el rango de fechas especificado. La lista puede estar vacía si no hay
     *         habitaciones disponibles.
     */
    @Transactional(readOnly = true)
    public List<RoomDisponibleResponse> getDisponibles(LocalDate fechaEntrada, LocalDate fechaSalida) {

        List<Room> disponibles = roomRepository.findRoomDisponibles(fechaEntrada, fechaSalida);

        return roomMapper.toRoomDisponiblesResponse(disponibles);
    }

    /**
     * Crea una nueva habitación en el sistema.
     *
     * Valida que no exista una habitación con el mismo número y que el tipo
     * de habitación sea válido (SUITE, INDIVIDUAL o DOBLE).
     *
     * Para habitaciones tipo SUITE, asigna los valores de capacidad y precio por noche
     * desde el request. Para INDIVIDUAL y DOBLE, se asignan valores por defecto.
     *
     * Validaciones:
     * - No debe existir otra habitación con el número proporcionado.
     * - El tipo de habitación debe ser válido (SUITE, INDIVIDUAL o DOBLE).
     * - Para SUITE: Se requieren capacidad y precio por noche válidos.
     *
     * @param request Objeto {@link RoomRequest} con los datos de la habitación a crear.
     *                Contiene los campos: numero, tipo, capacidad y precioPorNoche.
     *
     * @return {@link RoomResponse} con los datos de la habitación creada,
     *         incluyendo su ID asignado automáticamente, estado inicial
     *         y todos los campos especificados en el request.
     *
     * @throws IllegalArgumentException Si ya existe una habitación con el número proporcionado,
     *                                   o si el tipo de habitación no es válido.
     */
    public RoomResponse create(RoomRequest request) {

        if (roomRepository.existsByNumero(request.numero())) {
            throw new IllegalArgumentException("Ya existe una habitación con ese número");
        }

        Room room = new Room();

        room.setTipo(request.tipo());
        room.setNumero(request.numero());

        if (request.tipo() == RoomType.SUITE) {
            room.setCapacidad(request.capacidad());
            room.setPrecioPorNoche(request.precioPorNoche().intValue());
        } else if (request.tipo() == RoomType.INDIVIDUAL) {
        } else if (request.tipo() == RoomType.DOBLE) {
        } else {
            throw new IllegalArgumentException("Tipo de habitación no válido");
        }

        Room saved = roomRepository.save(room);

        return new RoomResponse(
                saved.getId(),
                saved.getNumero(),
                saved.getTipo(),
                saved.getCapacidad(),
                (double) saved.getPrecioPorNoche(),
                saved.getEstado()
        );
    }

    /**
     * Elimina una habitación existente del sistema.
     *
     * Verifica que la habitación con el ID proporcionado exista antes de proceder
     * a su eliminación. Si la habitación no existe, se lanzará una {@link RuntimeException}.
     *
     * @param id Identificador único de la habitación a eliminar. Debe ser un valor positivo y válido.
     *
     * @throws RuntimeException Si la habitación con el ID especificado no existe en el sistema.
     */
    public void delete(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Habitación no encontrada");
        }
        roomRepository.deleteById(id);
    }

    public void bloquearHabitacion(Long id) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Habitación no encontrada"));

        room.setEstado(RoomStatus.BLOQUEADA);

        roomRepository.save(room);
    }

}


