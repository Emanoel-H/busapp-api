package br.com.javamastery.busapp_api.controller;

import br.com.javamastery.busapp_api.dto.LoginRequest;
import br.com.javamastery.busapp_api.dto.LoginResponse;
import br.com.javamastery.busapp_api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.ok(service.login(loginRequest));
    }
}
