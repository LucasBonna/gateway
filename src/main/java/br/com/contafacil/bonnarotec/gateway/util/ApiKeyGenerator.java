package br.com.contafacil.bonnarotec.gateway.util;

import br.com.contafacil.bonnarotec.gateway.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ApiKeyGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int API_KEY_LENGTH = 20;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;

    @Autowired
    public ApiKeyGenerator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateUniqueApiKey() {
        String apiKey;
        do {
            apiKey = generateApiKey();
        } while (userRepository.existsByApiKey(apiKey));
        return apiKey;
    }

    private String generateApiKey() {
        StringBuilder apiKey = new StringBuilder(API_KEY_LENGTH);
        for (int i = 0; i < API_KEY_LENGTH; i++) {
            apiKey.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return apiKey.toString();
    }
}
