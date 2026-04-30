package org.example.projectfinder.controller;

import org.example.projectfinder.security.JwtService;
import org.example.projectfinder.model.dto.AuthRequest;
import org.example.projectfinder.model.dto.AuthResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {

        // Validates credentials against SecurityConfig users (USER/ADMIN)
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var userDetails = (org.springframework.security.core.userdetails.UserDetails)
                authentication.getPrincipal();

        String role = userDetails.getAuthorities()
                .iterator()
                .next()
                .getAuthority()
                .replace("ROLE_", ""); // Formats to: "role": "ADMIN"

        String token = jwtService.generateToken(request.getUsername(), role);

        return new AuthResponse(token);
    }
}