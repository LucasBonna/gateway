package br.com.contafacil.bonnarotec.gateway.service;

import br.com.contafacil.shared.bonnarotec.toolslib.domain.user.UserEntity;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<UserEntity> authenticate(String username, String password);
}
