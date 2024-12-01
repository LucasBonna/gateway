package br.com.contafacil.bonnarotec.gateway.service.implementation;

import br.com.contafacil.bonnarotec.gateway.client.RegistryServiceClient;
import br.com.contafacil.bonnarotec.gateway.exception.WrongCredentialsException;
import br.com.contafacil.bonnarotec.gateway.service.AuthService;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RegistryServiceClient registryServiceClient;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Mono<UserEntity> authenticate(String username, String password) {
        UserEntity user = registryServiceClient.findUserByUsername(username);
        
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return Mono.error(new WrongCredentialsException("Invalid username or password"));
        }
        
        return Mono.just(user);
    }
}
