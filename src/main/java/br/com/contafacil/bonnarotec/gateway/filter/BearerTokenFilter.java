package br.com.contafacil.bonnarotec.gateway.filter;

import br.com.contafacil.bonnarotec.gateway.client.RegistryServiceClient;
import br.com.contafacil.bonnarotec.gateway.exception.InvalidUserException;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.client.ClientDTO;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.client.ClientEntity;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.user.UserDTO;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.user.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class BearerTokenFilter implements WebFilter {

    private final ObjectMapper objectMapper;
    private final RegistryServiceClient registryServiceClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        
        if (isPublicPath(path)) {
            System.out.println("Public path: " + path);
            return chain.filter(exchange);
        }

        System.out.println("Protected path: " + path);

        String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header"));
        }

        String apiKey = authorization.substring(7);
        
        return Mono.fromCallable(() -> registryServiceClient.findUserByApiKey(apiKey))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(user -> {
                    if (user == null) {
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid API key"));
                    }
                    
                    return Mono.fromCallable(() -> registryServiceClient.findClientById(user.getClient().getId()))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(client -> {
                                if (client == null) {
                                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Client not found"));
                                }
                                return addHeadersToRequest(exchange, user, client)
                                        .then(chain.filter(exchange));
                            });
                })
                .onErrorResume(e -> Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage())));
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/auth") || 
               path.equals("/") || 
               path.equals("/actuator/health") ||
               path.startsWith("/swagger") ||
               path.startsWith("/webjars") ||
               path.startsWith("/v3/api-docs") ||
               path.contains("/cfregistry/api/v1/users/auth/login") ||
               path.equals("/cfemission/v3/api-docs") ||
               path.equals("/cfstorage/v3/api-docs") ||
               path.equals("/cfregistry/v3/api-docs");
    }

    private Mono<Void> addHeadersToRequest(ServerWebExchange exchange, UserEntity user, ClientEntity client) {
        try {
            UserDTO userDTO = toUserDTO(user);
            String userJson = objectMapper.writeValueAsString(userDTO);
            
            ClientDTO clientDTO = toClientDTO(client);
            String clientJson = objectMapper.writeValueAsString(clientDTO);

            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("X-User", userJson)
                    .header("X-Client", clientJson)
                    .build();

            return Mono.just(exchange.mutate().request(request).build())
                    .then();
        } catch (JsonProcessingException e) {
            return Mono.error(new InvalidUserException("Error processing user data"));
        }
    }

    private UserDTO toUserDTO(UserEntity user) {
        return new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getPassword(),
            user.getApiKey(),
            user.getRole(),
            user.getClient()
        );
    }

    private ClientDTO toClientDTO(ClientEntity client) {
        return new ClientDTO(
            client.getId(),
            client.getName(),
            client.getRole()
        );
    }
}