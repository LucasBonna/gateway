package br.com.contafacil.bonnarotec.gateway.client;

import br.com.contafacil.shared.bonnarotec.toolslib.domain.client.ClientEntity;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.user.UserEntity;
import reactor.core.publisher.Mono;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "registry-service", fallback = RegistryServiceFallback.class)
public interface RegistryServiceClient {

    @GetMapping("/api/v1/users/api-key/{apiKey}")
    UserEntity findUserByApiKey(@PathVariable("apiKey") String apiKey);

    @GetMapping("/api/v1/users/username/{username}")
    Mono<UserEntity> findUserByUsername(@PathVariable("username") String username);

    @GetMapping("/api/v1/clients/{id}")
    ClientEntity findClientById(@PathVariable("id") UUID id);
}
