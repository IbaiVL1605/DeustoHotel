package com.example.deusto_hotel.repository;

import com.example.deusto_hotel.model.Role;
import com.example.deusto_hotel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar las operaciones de base de datos de usuarios.
 * Extiende JpaRepository para proporcionar operaciones CRUD básicas y consultas personalizadas.
 *
 * @author Deusto Hotel Team
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Encuentra un usuario por su correo electrónico.
     *
     * @param email El correo electrónico del usuario a buscar
     * @return Optional con el usuario si existe, Optional vacío en caso contrario
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el correo electrónico especificado.
     *
     * @param email El correo electrónico a verificar
     * @return true si existe un usuario con ese correo, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Encuentra todos los usuarios con un estado de bloqueo específico.
     *
     * @param bloqueado true para buscar usuarios bloqueados, false para usuarios activos
     * @return Lista de usuarios con el estado de bloqueo especificado
     */
    List<User> findByBloqueado(boolean bloqueado);

    /**
     * Encuentra todos los usuarios con un rol específico.
     *
     * @param rol El rol de usuario a buscar
     * @return Lista de usuarios con el rol especificado
     */
    List<User> findByRol(Role rol);
}
