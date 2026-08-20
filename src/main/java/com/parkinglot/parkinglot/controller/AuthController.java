package com.parkinglot.parkinglot.controller;

import com.parkinglot.parkinglot.dto.AuthResponse;
import com.parkinglot.parkinglot.dto.LoginRequest;
import com.parkinglot.parkinglot.dto.UserResponse;
import com.parkinglot.parkinglot.exception.DuplicateEmailException;
import com.parkinglot.parkinglot.exception.InvalidCredentialsException;
import com.parkinglot.parkinglot.model.Role;
import com.parkinglot.parkinglot.model.User;
import com.parkinglot.parkinglot.repository.UserRepository;
import com.parkinglot.parkinglot.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email already registered: " + user.getEmail());
        }

        if (userRepository.count() == 0) {
            user.setRole(Role.ADMIN);
        } else if (user.getRole() != Role.VENDOR) {
            user.setRole(Role.CUSTOMER);
        }
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return toUserResponse(userRepository.save(user));
    }

    @GetMapping("/users")
    public List<UserResponse> getAllUsers(Authentication authentication) {
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid user"));

        if (currentUser.getRole() == Role.ADMIN) {
            return userRepository.findAll().stream()
                    .map(this::toUserResponse)
                    .toList();
        }

        return List.of(toUserResponse(currentUser));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        if (!user.isActive()) {
            throw new InvalidCredentialsException("This account is suspended");
        }

        return new AuthResponse(
                jwtUtil.generateToken(user.getEmail(), user.getRole().name()),
                user.getEmail(),
                user.getRole().name()
        );
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
