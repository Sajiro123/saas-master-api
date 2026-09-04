package com.saas.master.auth.controller;

import com.saas.master.auth.dto.AuthResponse;
import com.saas.master.auth.dto.LoginRequest;
import com.saas.master.auth.dto.ValidateTokenResponse;
import com.saas.master.auth.service.AuthService;
import com.saas.master.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación Global", description = "Endpoints para inicio de sesión central y validación de tokens JWT")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Inicio de Sesión Central", description = "Autentica al usuario global, resuelve el tenant y emite el JWT con claims.")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Inicio de sesión exitoso", response));
    }

    @GetMapping("/validate")
    @Operation(summary = "Validar Token JWT", description = "Verifica la validez y extrae claims de un token emitido.")
    public ResponseEntity<ApiResponse<ValidateTokenResponse>> validateToken(@RequestHeader("Authorization") String authHeader) {
        ValidateTokenResponse response = authService.validateToken(authHeader);
        return ResponseEntity.ok(ApiResponse.ok("Validación de token", response));
    }
}
