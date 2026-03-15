package com.example.deusto_hotel.mapper;

import com.example.deusto_hotel.dto.UserResponse;
import com.example.deusto_hotel.dto.UserRequest;
import com.example.deusto_hotel.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // Entity -> DTO Response
    UserResponse toResponse(User user);

    // DTO Request -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target="creadoEn", ignore = true)
    @Mapping(target="rol", ignore = true)
    @Mapping(target="bloqueado", ignore = true)
    @Mapping(target="cancelaciones", ignore = true)
    @Mapping(target="roomBookings", ignore = true)
    @Mapping(target="courtBookings", ignore = true)
    @Mapping(target="reviews", ignore = true)
    User toEntity(UserRequest request);

    // Actualizar Entity con DTO Request
    @Mapping(target = "id", ignore = true)
    @Mapping(target="creadoEn", ignore = true)
    @Mapping(target="rol", ignore = true)
    @Mapping(target="bloqueado", ignore = true)
    @Mapping(target="cancelaciones", ignore = true)
    @Mapping(target="roomBookings", ignore = true)
    @Mapping(target="courtBookings", ignore = true)
    @Mapping(target="reviews", ignore = true)
    void updateEntityFromRequest(UserRequest request, @MappingTarget User user);
}
