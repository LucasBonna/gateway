package br.com.contafacil.bonnarotec.gateway.controller;

import br.com.contafacil.bonnarotec.gateway.dto.AuthRequestDTO;
import br.com.contafacil.bonnarotec.gateway.service.AuthService;
import br.com.contafacil.bonnarotec.gateway.util.JwtUtil;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.user.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Authenticate user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Authentication successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public Mono<ResponseEntity<Map<String, String>>> login(@Valid @RequestBody AuthRequestDTO request) {
        return authService.authenticate(request.getUsername(), request.getPassword())
                .map(user -> {
                    String token = jwtUtil.generateToken(user);
                    Map<String, String> response = new HashMap<>();
                    response.put("token", token);
                    return ResponseEntity.ok(response);
                });
    }
}
