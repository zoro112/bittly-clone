package com.bitly.bittly_clone.services;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.method.P;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bitly.bittly_clone.dto.UserCreateDTO;
import com.bitly.bittly_clone.dto.UserDTO;
import com.bitly.bittly_clone.dto.UserLoginDTO;
import com.bitly.bittly_clone.model.LoginReponse;
import com.bitly.bittly_clone.model.User;
import com.bitly.bittly_clone.repository.UserRepo;
import com.bitly.bittly_clone.security.JwtService;

import ch.qos.logback.core.model.Model;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserDTO register(UserCreateDTO userCreateDTO) {
        System.out.println("Registering user: " + userCreateDTO.getUsername());
        User user = modelMapper.map(userCreateDTO, User.class);
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return modelMapper.map(userRepo.save(user), UserDTO.class);
    }

    public LoginReponse login(UserLoginDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        User userA = userRepo.findByUsername(user.getUsername()).orElse(null);

        if (userA != null) {
            String jwtToken = jwtService.generateToken(userA);
            LoginReponse loginReponse = new LoginReponse();
            loginReponse.setExpirationTime(jwtService.extractExpiration(jwtToken).getTime());
            loginReponse.setToken(jwtToken);
            return loginReponse;
        } else {
            throw new RuntimeException("User not found");
        }

    }
}
