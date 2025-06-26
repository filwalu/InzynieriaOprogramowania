package com.essa.dto;

import lombok.Data;

@Data
public class PermissionDTO {
    private Long id;
    private String permission;
    private String description;
    private Long roleId;
}