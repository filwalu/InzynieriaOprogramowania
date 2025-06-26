package com.essa.mapper;

import com.essa.dto.PermissionDTO;
import com.essa.model.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionDTO toDTO(Permission permission);
    Permission toEntity(PermissionDTO dto);
}