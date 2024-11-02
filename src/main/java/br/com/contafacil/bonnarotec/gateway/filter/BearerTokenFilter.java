package br.com.contafacil.bonnarotec.gateway.filter;

import br.com.contafacil.bonnarotec.gateway.domain.client.ClientRepository;
import br.com.contafacil.bonnarotec.gateway.domain.user.UserRepository;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.client.ClientEntity;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.user.UserDTO;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.client.ClientDTO;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.user.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class BearerTokenFilter implements WebFilter {

    private ClientRepository clientRepository;
    private UserRepository userRepository;
    private ObjectMapper objectMapper;

    private final PathMatcher pathMatcher = new AntPathMatcher();

    public BearerTokenFilter(ClientRepository clientRepository, UserRepository userRepository, ObjectMapper objectMapper) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("null")
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (isExcludedPath(path)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        System.out.println("Token: " + token);

        Optional<UserEntity> user = this.userRepository.findByApiKey(token);
        if (user.isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        System.out.println("User passou");

        Optional<ClientEntity> client = this.clientRepository.findByUsersContaining(user.get());
        if (client.isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        System.out.println("Client passou");

        try {
            UserDTO userDTO = toUserDTO(user.get());
            String userJson = this.objectMapper.writeValueAsString(userDTO);

            ClientDTO clientDTO = toClientDTO(client.get());
            String clientJson = this.objectMapper.writeValueAsString(clientDTO);

            System.out.println("userJson: " + userJson);

            System.out.println("clientJson: " + clientJson);

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User", userJson)
                    .header("X-Client", clientJson)
                    .build();

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(mutatedRequest)
                    .build();

            return chain.filter(mutatedExchange);
        } catch (JsonProcessingException e) {
            System.out.println("Erro ao processar JSON" + e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isExcludedPath(String path) {
        return pathMatcher.match("/", path) ||
                pathMatcher.match("/docs", path) ||
                pathMatcher.match("/swagger", path) ||
                pathMatcher.match("/swagger/index.html", path) ||
                pathMatcher.match("/swagger-ui/**", path) ||
                pathMatcher.match("/swagger-ui.html", path) ||
                pathMatcher.match("/webjars/**", path) ||
                pathMatcher.match("/v3/api-docs/**", path) ||
                pathMatcher.match("/cfemission/v3/api-docs", path) ||
                pathMatcher.match("/cfstorage/v3/api-docs", path) ||
                pathMatcher.match("/v3/api-docs", path);
    }

    private UserDTO toUserDTO(UserEntity user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getPassword(), user.getApiKey(), user.getClient());
    }

    // Converte ClientEntity para ClientDTO
    private ClientDTO toClientDTO(ClientEntity client) {
        return new ClientDTO(client.getId(), client.getName());
    }
}