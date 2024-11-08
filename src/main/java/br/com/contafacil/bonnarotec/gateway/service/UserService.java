package br.com.contafacil.bonnarotec.gateway.service;

import br.com.contafacil.bonnarotec.gateway.domain.PagedResponseDTO;
import br.com.contafacil.bonnarotec.gateway.domain.user.UserRequestDTO;
import br.com.contafacil.bonnarotec.gateway.domain.user.UserResponseDTO;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserService {
    Mono<UserResponseDTO> createUser(UserRequestDTO userRequest);
    Mono<PagedResponseDTO<UserResponseDTO>> getAllUsers(int page, int size, String sortBy, String sortDir, UUID clientId);
    Mono<UserResponseDTO> getUserById(UUID id);
//    Mono<UserResponseDTO> updateUser(UUID id, UserRequestDTO userRequest);
    Mono<Void> deleteUser(UUID id);
}
