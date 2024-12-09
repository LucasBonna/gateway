package br.com.contafacil.bonnarotec.gateway.client;

import br.com.contafacil.shared.bonnarotec.toolslib.domain.client.ClientEntity;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.user.UserEntity;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class RegistryServiceFallback implements RegistryServiceClient {

    @Override
    public UserEntity findUserByApiKey(String apiKey) {
        log.error("Fallback: Error fetching user with API key: {}", apiKey);
        return null;
    }

    @Override
    public Mono<UserEntity> findUserByUsername(String username) {
        log.error("Fallback: Error fetching user with username: {}", username);
        return null;
    }

    @Override
    public ClientEntity findClientById(UUID id) {
        log.error("Fallback: Error fetching client with ID: {}", id);
        return null;
    }
}
