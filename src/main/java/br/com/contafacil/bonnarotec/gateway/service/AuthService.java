package br.com.contafacil.bonnarotec.gateway.service;

import br.com.contafacil.shared.bonnarotec.toolslib.domain.user.UserEntity;

public interface AuthService {
    UserEntity authenticate(String username, String password);
}
