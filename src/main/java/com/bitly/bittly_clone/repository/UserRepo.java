package com.bitly.bittly_clone.repository;

import java.lang.StackWalker.Option;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bitly.bittly_clone.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    // Custom query methods can be defined here if needed
    // For example, you can find users by username or email
    // User findByUsername(String username);
    // User findByEmail(String email);
    Optional<User> findByUsername(String username); // Optional to handle cases where user is not found
}
