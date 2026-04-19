package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.dto.UserRequest;
import com.example.deusto_hotel.dto.UserResponse;
import com.example.deusto_hotel.exception.Excepciones;
import com.example.deusto_hotel.mapper.UserMapper;
import com.example.deusto_hotel.model.Role;
import com.example.deusto_hotel.model.User;
import com.example.deusto_hotel.repository.UserRepository;
import com.example.deusto_hotel.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    public void testRegistrarUsuario_Success() {
        // Crear usuario con datos válidos
        UserRequest request = new UserRequest("Juan", "a@gmail.com", "12345678");

        // Mock del User que se espera guardar en la base de datos (sin ID, solo con los datos del request)
        User mockUserFromMapper = new User();
        mockUserFromMapper.setNombre("Juan");
        mockUserFromMapper.setEmail("a@gmail.com");
        mockUserFromMapper.setPassword("12345678");

        // Mock del User que se espera obtener después de guardar en la base de datos (con ID y otros campos)
        User mockUserSaved = new User();
        mockUserSaved.setId(1L);
        mockUserSaved.setNombre("Juan");
        mockUserSaved.setEmail("a@gmail.com");
        mockUserSaved.setPassword("12345678");
        mockUserSaved.setRol(Role.CLIENT);
        mockUserSaved.setBloqueado(false);
        mockUserSaved.setCreadoEn(LocalDateTime.now());

        // Mock del UserResponse final
        UserResponse mockResponse = new UserResponse(
                1L,
                "Juan",
                "a@gmail.com",
                Role.CLIENT,
                false,
                LocalDateTime.now()
        );

        // Config Mocks
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(mockUserFromMapper);
        when(userRepository.save(any(User.class))).thenReturn(mockUserSaved);
        when(userMapper.toResponse(mockUserSaved)).thenReturn(mockResponse);

        // Ejecución del metodo toEntity del mapper para convertir el UserRequest a User
        UserResponse response = userService.create(request);

        // Verificar resultados
        assertNotNull(response, "La respuesta no debería ser null");
        assertEquals("a@gmail.com", response.email());
        assertEquals("Juan", response.nombre());
        assertEquals(Role.CLIENT, response.rol());
        assertFalse(response.bloqueado());

        // Verificar Mocks
        verify(userRepository, times(1)).existsByEmail(request.email());
        verify(userMapper, times(1)).toEntity(request);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toResponse(mockUserSaved);
    }

    @Test
    public void testRegistrarUsuario_EmailAlreadyExists() {
        // Crear usuario con un email que ya existe
        UserRequest request = new UserRequest("Juan", "a@gmail.com", "12345678");

        // Simular que el email ya existe en la base de datos
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        // Llamar al metodo de registro y verificar que se lance la excepción correcta
        // Verificar que no se intente guardar el usuario si el email ya existe
        assertThrows(Excepciones.EmailYaRegistradoException.class, () -> userService.create(request));
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toEntity(any());
        verify(userMapper, never()).toResponse(any());
    }
}