package com.bitly.bittly_clone.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bitly.bittly_clone.dto.UserCreateDTO;
import com.bitly.bittly_clone.dto.UserDTO;
import com.bitly.bittly_clone.dto.UserLoginDTO;
import com.bitly.bittly_clone.model.LoginReponse;
import com.bitly.bittly_clone.services.AuthenticationService;
import com.bitly.bittly_clone.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class User_Control {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserCreateDTO user) {
        // TODO: process POST request
        System.out.println("Registering user: " + user.getUsername());
        return ResponseEntity.ok(authenticationService.register(user));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginReponse> loginUser(@RequestBody UserLoginDTO UserLoginDTO) {
        return ResponseEntity.ok(authenticationService.login(UserLoginDTO));
    }

}
