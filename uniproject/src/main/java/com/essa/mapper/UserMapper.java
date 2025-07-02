package com.essa.mapper;

import com.essa.dto.UserCreateDTO;
import com.essa.dto.UserDTO;
import com.essa.dto.UserUpdateDTO;
import com.essa.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    // Entity -> DTO
    @Mapping(target = "roleId", source = "role.id")
    UserDTO toDTO(User user);

    // CreateDTO -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "role", ignore = true) // Będzie ustawiane w service
    User toEntity(UserCreateDTO dto);

    // UpdateDTO -> Entity (partial update)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "username", ignore = true) // Username nie może być zmieniany
    @Mapping(target = "role", ignore = true) // Będzie ustawiane w service
    void updateEntityFromDto(UserUpdateDTO dto, @MappingTarget User user);
}