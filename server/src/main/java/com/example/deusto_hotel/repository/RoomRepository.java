package com.example.deusto_hotel.repository;

import com.example.deusto_hotel.model.Room;
import com.example.deusto_hotel.model.RoomStatus;
import com.example.deusto_hotel.model.RoomType;
import jakarta.validation.constraints.Positive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar las operaciones de base de datos de habitaciones.
 * Extiende JpaRepository para proporcionar operaciones CRUD básicas y consultas personalizadas.
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * Encuentra todas las habitaciones con un estado específico.
     * @param estado El estado de la habitación
     * @return Lista de habitaciones con el estado dado
     */
    List<Room> findByEstado(RoomStatus estado);

    /**
     * Encuentra todas las habitaciones de un tipo específico.
     * @param tipo El tipo de habitación
     * @return Lista de habitaciones del tipo dado
     */
    List<Room> findByTipo(RoomType tipo);

    /**
     * Encuentra una habitación por su número.
     * @param numero El número de la habitación
     * @return Optional con la habitación si existe, Optional vacío en caso contrario
     */
    Optional<Room> findByNumero(String numero);

    /**
     * Verifica si existe una habitación con el número especificado.
     * @param numero El número de la habitación
     * @return true si existe la habitación, false en caso contrario
     */
    boolean existsByNumero(String numero);

    /**
     * Encuentra habitaciones disponibles (no reservadas) para un rango de fechas específico.
     * Solo devuelve habitaciones con estado DISPONIBLE que no tengan reservas superpuestas (excepto canceladas).
     * @param fechaEntrada La fecha de entrada
     * @param fechaSalida La fecha de salida
     * @return Lista de habitaciones disponibles ordenadas por número
     */
    @Query("SELECT r FROM Room r WHERE r.estado = 'DISPONIBLE' AND " +
           "r.id NOT IN (SELECT rb.habitacion.id FROM RoomBooking rb " +
           "WHERE rb.estado != 'CANCELADA' AND rb.checkIn < :fechaSalida AND rb.checkOut > :fechaEntrada) " +
           "ORDER BY r.numero")
    List<Room> findRoomDisponibles(LocalDate fechaEntrada, LocalDate fechaSalida);

    /**
     * Encuentra una habitación por su ID y tipo.
     * @param aLong El ID de la habitación
     * @param roomType El tipo de habitación
     * @return Optional con la habitación si existe, Optional vacío en caso contrario
     */
    Optional<Room> findByIdAndTipo(@Positive Long aLong, RoomType roomType);
}
