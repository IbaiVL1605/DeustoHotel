package com.example.deusto_hotel.mapper;

import com.example.deusto_hotel.dto.UserRequest;
import com.example.deusto_hotel.dto.UserResponse;
import com.example.deusto_hotel.model.Role;
import com.example.deusto_hotel.model.User;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-25T10:55:32+0200",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.1.jar, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse toResponse(User user) {
        if ( user == null ) {
            return null;
        }

        Long id = null;
        String nombre = null;
        String email = null;
        Role rol = null;
        boolean bloqueado = false;
        LocalDateTime creadoEn = null;

        id = user.getId();
        nombre = user.getNombre();
        email = user.getEmail();
        rol = user.getRol();
        bloqueado = user.isBloqueado();
        creadoEn = user.getCreadoEn();

        UserResponse userResponse = new UserResponse( id, nombre, email, rol, bloqueado, creadoEn );

        return userResponse;
    }

    @Override
    public User toEntity(UserRequest request) {
        if ( request == null ) {
            return null;
        }

        User user = new User();

        user.setNombre( request.nombre() );
        user.setEmail( request.email() );
        user.setPassword( request.password() );

        return user;
    }

    @Override
    public void updateEntityFromRequest(UserRequest request, User user) {
        if ( request == null ) {
            return;
        }

        user.setNombre( request.nombre() );
        user.setEmail( request.email() );
        user.setPassword( request.password() );
    }
}
