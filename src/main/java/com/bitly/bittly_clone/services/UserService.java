package com.bitly.bittly_clone.services;

import org.springframework.stereotype.Service;

import com.bitly.bittly_clone.dto.UserCreateDTO;
import com.bitly.bittly_clone.dto.UserDTO;
import com.bitly.bittly_clone.model.User;
import com.bitly.bittly_clone.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    public UserDTO getUserById(Long id) {

        // Implement the logic to retrieve a user by ID from the database
        // For example, you can use the UserRepo to find the user by ID
        User user = userRepo.findById(id).orElse(null); // Return null if not found
        if (user != null) {
            // Convert User entity to UserDTO using ModelMapper
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            return userDTO;
        }
        return null; // User not found

    }

    public UserDTO saveUser(UserCreateDTO userCreateDTO) {
        // Implement the logic to save a user to the database

        User user = modelMapper.map(userCreateDTO, User.class);
        userRepo.save(user);
        return modelMapper.map(userRepo.save(user), UserDTO.class);
    }

    public User getUserByUsername(String name) {
        return userRepo.findByUsername(name).orElse(null);
    }

}
