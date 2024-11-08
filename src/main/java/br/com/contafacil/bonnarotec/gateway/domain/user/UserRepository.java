package br.com.contafacil.bonnarotec.gateway.domain.user;

import br.com.contafacil.shared.bonnarotec.toolslib.domain.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByUsernameAndClientId(String username, UUID clientId);

    Optional<UserEntity> findByApiKey(String apiKey);

    boolean existsByApiKey(String apiKey);

    Optional<UserEntity> findByIdAndDeletedAtIsNull(UUID id);

    @Query("SELECT u FROM UserEntity u WHERE u.deletedAt IS NULL")
    Page<UserEntity> findAllActiveUsers(Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE u.client.id = :clientId AND u.deletedAt IS NULL")
    Page<UserEntity> findAllActiveUsersByClientId(@Param("clientId") UUID clientId, Pageable pageable);
}
