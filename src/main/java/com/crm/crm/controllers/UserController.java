package com.crm.crm.controllers;

import com.crm.crm.dtos.UserDto;
import com.crm.crm.exceptions.InvalidPayloadException;
import com.crm.crm.exceptions.UserIdAlreadyExistException;
import com.crm.crm.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> saveUser(@RequestBody UserDto userDto) {
        if (Objects.isNull(userDto)) {
            throw new InvalidPayloadException("Payload cannot be Null");
        }
        if(userService.findByEmail(userDto.getEmail())){
            throw new UserIdAlreadyExistException("Email is already taken");
        }
        return userService.saveUser(userDto);
    }
}