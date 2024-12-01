package br.com.contafacil.bonnarotec.gateway.util;

import br.com.contafacil.shared.bonnarotec.toolslib.domain.user.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final ObjectMapper objectMapper;

    public JwtUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String generateToken(UserEntity user) {
        try {
            user.setPassword(null);

            Map<String, Object> userClaims = new HashMap<>();
            userClaims.put("id", user.getId());
            userClaims.put("username", user.getUsername());
            userClaims.put("apiKey", user.getApiKey());

            if (user.getClient() != null) {
                Map<String, Object> clientMap = new HashMap<>();
                clientMap.put("id", user.getClient().getId());
                clientMap.put("name", user.getClient().getName());
                clientMap.put("role", user.getClient().getRole());

                userClaims.put("client", clientMap);
            } else {
                userClaims.put("client", null);
            }

            userClaims.put("createdAt", user.getCreatedAt());
            userClaims.put("updatedAt", user.getUpdatedAt());
            userClaims.put("deletedAt", user.getDeletedAt());

            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

            return Jwts.builder()
                    .subject(user.getUsername())
                    .claim("user", userClaims)
                    .issuedAt(Date.from(Instant.now()))
                    .expiration(Date.from(Instant.now().plus(5, ChronoUnit.HOURS)))
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar o usuário para o JWT", e);
        }
    }
}
