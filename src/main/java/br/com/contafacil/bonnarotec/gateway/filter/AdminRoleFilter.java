package br.com.contafacil.bonnarotec.gateway.filter;

import br.com.contafacil.shared.bonnarotec.toolslib.domain.client.ClientDTO;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.client.ClientRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(2)
public class AdminRoleFilter implements WebFilter {

    private final PathMatcher pathMatcher = new AntPathMatcher();
    private final ObjectMapper objectMapper;

    @Autowired
    public AdminRoleFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        if (!isAdminPath(path)) {
            return chain.filter(exchange);
        }

        String clientHeader = request.getHeaders().getFirst("X-Client");
        if (clientHeader == null) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        try {
            ClientDTO clientDTO = objectMapper.readValue(clientHeader, ClientDTO.class);
            if (clientDTO.getRole() != null && clientDTO.getRole().equals(ClientRole.ADMIN)) {
                return chain.filter(exchange);
            } else {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        } catch (JsonProcessingException e) {
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isAdminPath(String path) {
        return pathMatcher.match("/admin/**", path);
    }
}
