package br.com.contafacil.bonnarotec.gateway.domain.user;

import br.com.contafacil.shared.bonnarotec.toolslib.domain.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByApiKey(String apiKey);
}
