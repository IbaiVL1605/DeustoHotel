package com.example.deusto_hotel.repository;

import com.example.deusto_hotel.model.RoomBooking;
import com.example.deusto_hotel.model.RoomBookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
/**
 * Repositorio para gestionar las operaciones de base de datos de reservas de habitaciones.
 * Extiende JpaRepository para proporcionar operaciones CRUD básicas.
 */
public interface RoomBookingRepository extends JpaRepository<RoomBooking, Long> {

    /**
     * Encuentra todas las reservas de un cliente específico.
     * @param clienteId El ID del cliente
     * @return Lista de reservas del cliente
     */
    List<RoomBooking> findByClienteId(Long clienteId);

    /**
     * Encuentra todas las reservas de una habitación específica.
     * @param habitacionId El ID de la habitación
     * @return Lista de reservas de la habitación
     */
    List<RoomBooking> findByHabitacionId(Long habitacionId);

    /**
     * Encuentra todas las reservas con un estado específico.
     * @param estado El estado de la reserva
     * @return Lista de reservas con el estado dado
     */
    List<RoomBooking> findByEstado(RoomBookingStatus estado);


    /**
     * Encuentra reservas que se solapan con las fechas dadas para una habitación específica.
     * Excluye reservas canceladas.
     * @param habitacionId El ID de la habitación
     * @param checkIn La fecha de check-in
     * @param checkOut La fecha de check-out
     * @return Lista de reservas que se solapan
     */
    @Query("SELECT r FROM RoomBooking r WHERE r.habitacion.id = :habitacionId " +
           "AND r.estado != 'CANCELADA' " +
           "AND r.checkIn < :checkOut AND r.checkOut > :checkIn")
    List<RoomBooking> findSolapamientos(@Param("habitacionId") Long habitacionId,
                                        @Param("checkIn") LocalDate checkIn,
                                        @Param("checkOut") LocalDate checkOut);



    /**
     * Encuentra todas las reservas de un cliente específico con un estado dado.
     * @param clienteId El ID del cliente
     * @param estado El estado de la reserva
     * @return Lista de reservas del cliente con el estado especificado
     */
    List<RoomBooking> findByClienteIdAndEstado(Long clienteId,
                                               RoomBookingStatus estado);

    Optional<RoomBooking> findFirstByClienteIdAndEstadoOrderByCreadaEnAsc(Long clienteId,
                                                                         RoomBookingStatus estado);
}
