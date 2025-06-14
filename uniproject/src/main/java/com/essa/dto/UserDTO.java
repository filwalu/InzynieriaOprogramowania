package com.essa.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private Long roleId;
    private String roleName;
}