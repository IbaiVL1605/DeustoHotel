package com.example.deusto_hotel.repository;

import com.example.deusto_hotel.model.Court;
import com.example.deusto_hotel.model.CourtStatus;
import com.example.deusto_hotel.model.CourtType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio encargado de la gestión de pistas deportivas.
 * <p>
 * Proporciona métodos de acceso a datos relacionados con
 * búsquedas y filtrados de pistas.
 * </p>
 */
@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {

    /**
     * Obtiene las pistas filtradas por estado.
     *
     * @param estado estado de la pista
     * @return lista de pistas con el estado indicado
     */
    List<Court> findByEstado(CourtStatus estado);

    /**
     * Obtiene las pistas filtradas por tipo.
     *
     * @param tipo tipo de pista
     * @return lista de pistas del tipo indicado
     */
    List<Court> findByTipo(CourtType tipo);

    /**
     * Obtiene las pistas filtradas por tipo y estado.
     *
     * @param tipo tipo de pista
     * @param estado estado de la pista
     * @return lista de pistas filtradas
     */
    List<Court> findByTipoAndEstado(CourtType tipo, CourtStatus estado);

    /**
     * Comprueba si ya existe una pista con un nombre concreto.
     *
     * @param nombre nombre de la pista
     * @return true si existe una pista con ese nombre,
     * false en caso contrario
     */
    boolean existsByNombre(String nombre);
}
