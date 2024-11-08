package br.com.contafacil.bonnarotec.gateway.controller;

import br.com.contafacil.bonnarotec.gateway.domain.client.ClientResponseDTO;
import br.com.contafacil.bonnarotec.gateway.domain.user.AuthRequestDTO;
import br.com.contafacil.bonnarotec.gateway.domain.user.UserRepository;
import br.com.contafacil.bonnarotec.gateway.exception.WrongCredentialsException;
import br.com.contafacil.bonnarotec.gateway.service.AuthService;
import br.com.contafacil.bonnarotec.gateway.util.JwtUtil;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.user.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Rotas de disponiveis para Autenticacao")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "Login", description = "Retorna o JWT contendo os dados do usuário")
    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, String>>> login(@RequestBody @Valid AuthRequestDTO authRequestDTO) {

        UserEntity authenticatedUser = authService.authenticate(authRequestDTO.username(), authRequestDTO.password());
        String token = jwtUtil.generateToken(authenticatedUser);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("token", token);

        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseBody));
    }
}
