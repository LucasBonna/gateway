package br.com.contafacil.bonnarotec.gateway.service.implementation;

import br.com.contafacil.bonnarotec.gateway.domain.user.UserRepository;
import br.com.contafacil.bonnarotec.gateway.exception.WrongCredentialsException;
import br.com.contafacil.bonnarotec.gateway.service.AuthService;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.user.UserEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public UserEntity authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> {
                    user.setPassword(null);
                    return user;
                })
                .orElseThrow(() -> new WrongCredentialsException("Credenciais inválidas"));
    }

}
