package com.essa.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String password;
    private String firstname;
    private String lastname;
    private String email;
    private Long roleId;
}