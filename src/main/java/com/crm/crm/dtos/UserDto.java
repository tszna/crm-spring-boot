package com.crm.crm.dtos;

import lombok.Data;

@Data
public class UserDto {
    private long id;
    private String name;
    private String email;
    private String password;
}