package com.example.deusto_hotel.repository;

import com.example.deusto_hotel.model.Role;
import com.example.deusto_hotel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByBloqueado(boolean bloqueado);

    List<User> findByRol(Role rol);
}
