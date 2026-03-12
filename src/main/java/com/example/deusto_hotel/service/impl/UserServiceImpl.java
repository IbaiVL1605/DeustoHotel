package com.example.deusto_hotel.service.impl;

import com.example.deusto_hotel.dto.UserRequest;
import com.example.deusto_hotel.dto.UserResponse;
import com.example.deusto_hotel.repository.UserRepository;
import com.example.deusto_hotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    public UserResponse create(UserRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    public UserResponse update(Long id, UserRequest request) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Long id) {
        // TODO: Implementar
        throw new UnsupportedOperationException();
    }
}
