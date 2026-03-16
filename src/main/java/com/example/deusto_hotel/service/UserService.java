package com.example.deusto_hotel.service;

import com.example.deusto_hotel.dto.UserRequest;
import com.example.deusto_hotel.dto.UserResponse;
import com.example.deusto_hotel.mapper.UserMapper;
import com.example.deusto_hotel.model.Role;
import com.example.deusto_hotel.model.User;
import com.example.deusto_hotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    public UserResponse create(UserRequest request) {
        // Usar Mapper para convertir UserRequest a User, guardar en la base de datos y luego convertir a UserResponse
        User usuario = userMapper.toEntity(request);

        usuario.setRol(Role.CLIENT);   // o USER según tu enum
        usuario.setBloqueado(false);

        usuario = userRepository.save(usuario);

        return userMapper.toResponse(usuario);
    }

    
    public UserResponse update(Long id, UserRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    
    public void delete(Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }
}
