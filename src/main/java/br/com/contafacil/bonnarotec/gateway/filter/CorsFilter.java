package br.com.contafacil.bonnarotec.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(CorsFilter.class);

    @Value("${frontend_url}")
    private String frontendUrl;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        logger.info("Configurando CORS para origin: {}", frontendUrl);

        exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", frontendUrl);
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization, Cookie");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Credentials", "true");
        exchange.getResponse().getHeaders().add("Access-Control-Max-Age", "3600");
        exchange.getResponse().getHeaders().add("Access-Control-Expose-Headers", "Authorization, Set-Cookie");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequest().getMethod().name())) {
            logger.info("Requisição de preflight (OPTIONS) detectada. Respondendo com 200 OK.");
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.OK);
            return Mono.empty();
        }

        return chain.filter(exchange);
    }
}
