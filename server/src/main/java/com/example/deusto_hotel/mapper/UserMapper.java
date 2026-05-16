package com.example.deusto_hotel.mapper;

import com.example.deusto_hotel.dto.UserResponse;
import com.example.deusto_hotel.dto.UserRequest;
import com.example.deusto_hotel.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper para la transformación de entidades {@link User} y objetos DTO.
 *
 * Proporciona métodos para convertir entre objetos DTO (UserRequest y UserResponse)
 * y la entidad de dominio User. Utiliza MapStruct para generar automáticamente
 * las implementaciones de mapeo.
 *
 * @author Deusto Hotel Team
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Convierte una entidad {@link User} a su correspondiente {@link UserResponse}.
     *
     * Realiza el mapeo de la entidad User a un DTO de respuesta, incluyendo todos
     * los campos necesarios para la representación de usuario en las respuestas HTTP.
     *
     * @param user la entidad User a convertir
     * @return {@link UserResponse} con los datos del usuario convertidos, o null si user es null
     */
    UserResponse toResponse(User user);

    /**
     * Convierte un DTO de solicitud {@link UserRequest} a la entidad {@link User}.
     *
     * Realiza el mapeo del DTO de solicitud a la entidad User. Los campos como id,
     * fechaCreación, rol, estado de bloqueo y relaciones se ignoran en este mapeo,
     * ya que son establecidos por el servicio o la base de datos.
     *
     * @param request el DTO UserRequest con los datos a convertir
     * @return {@link User} con los datos del usuario mapeados, o null si request es null
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target="creadoEn", ignore = true)
    @Mapping(target="rol", ignore = true)
    @Mapping(target="bloqueado", ignore = true)
    @Mapping(target="cancelaciones", ignore = true)
    @Mapping(target="roomBookings", ignore = true)
    @Mapping(target="courtBookings", ignore = true)
    @Mapping(target="reviews", ignore = true)
    User toEntity(UserRequest request);

    /**
     * Actualiza una entidad {@link User} existente con los datos de un DTO de solicitud {@link UserRequest}.
     *
     * Realiza el mapeo del DTO de solicitud sobre la entidad existing, actualizando solo los campos
     * mapeables. Los campos como id, fechaCreación, rol, estado de bloqueo y relaciones se ignoran
     * para preservar los datos existentes.
     *
     * @param request el DTO UserRequest con los nuevos datos
     * @param user la entidad User a actualizar (target del mapeo)
     */
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
