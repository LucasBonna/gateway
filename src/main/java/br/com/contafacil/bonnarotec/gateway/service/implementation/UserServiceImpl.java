package br.com.contafacil.bonnarotec.gateway.service.implementation;

import br.com.contafacil.bonnarotec.gateway.domain.PagedResponseDTO;
import br.com.contafacil.bonnarotec.gateway.domain.client.ClientRepository;
import br.com.contafacil.bonnarotec.gateway.domain.user.UserRepository;
import br.com.contafacil.bonnarotec.gateway.domain.user.UserRequestDTO;
import br.com.contafacil.bonnarotec.gateway.domain.user.UserResponseDTO;
import br.com.contafacil.bonnarotec.gateway.exception.DuplicateUsernameException;
import br.com.contafacil.bonnarotec.gateway.service.UserService;
import br.com.contafacil.bonnarotec.gateway.util.ApiKeyGenerator;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.client.ClientDTO;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.client.ClientEntity;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApiKeyGenerator apiKeyGenerator;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ClientRepository clientRepository, PasswordEncoder passwordEncoder, ApiKeyGenerator apiKeyGenerator) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.apiKeyGenerator = apiKeyGenerator;
    }

    @Override
    public Mono<UserResponseDTO> createUser(UserRequestDTO userRequest) {
        // Verifica se o cliente existe
        return Mono.<Optional<ClientEntity>>fromCallable(() -> clientRepository.findById(userRequest.getClientId()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalClient -> Mono.justOrEmpty(optionalClient))
                .flatMap(client -> {
                    // Verifica se o username já existe para este cliente
                    return Mono.<Boolean>fromCallable(() -> userRepository.findByUsernameAndClientId(userRequest.getUsername(), userRequest.getClientId()).isPresent())
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(exists -> {
                                if (exists) {
                                    return Mono.<UserResponseDTO>error(new DuplicateUsernameException("Username '" + userRequest.getUsername() + "' já existe para este cliente."));
                                }
                                return Mono.just(client);
                            });
                })
                .flatMap(client -> {
                    // Cria a entidade User
                    UserEntity user = new UserEntity();
                    user.setUsername(userRequest.getUsername());
                    user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

                    // Gera a apiKey automaticamente
                    String generatedApiKey = apiKeyGenerator.generateUniqueApiKey();
                    user.setApiKey(generatedApiKey);

                    user.setClient((ClientEntity) client);

                    // Salva o usuário
                    return Mono.<UserEntity>fromCallable(() -> userRepository.save(user))
                            .subscribeOn(Schedulers.boundedElastic())
                            .map(this::mapToUserResponseDTO);
                })
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Cliente não encontrado com id: " + userRequest.getClientId())));
    }

    @Override
    public Mono<PagedResponseDTO<UserResponseDTO>> getAllUsers(int page, int size, String sortBy, String sortDir, UUID clientId) {
        return Mono.fromCallable(() -> {
                    Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
                    Pageable pageable = PageRequest.of(page, size, sort);
                    Page<UserEntity> userPage;

                    if (clientId != null) {
                        userPage = userRepository.findAllActiveUsersByClientId(clientId, pageable);
                    } else {
                        userPage = userRepository.findAllActiveUsers(pageable);
                    }

                    List<UserResponseDTO> content = userPage.getContent()
                            .stream()
                            .map(this::mapToUserResponseDTO)
                            .collect(Collectors.toList());

                    return new PagedResponseDTO<>(
                            content,
                            userPage.getNumber(),
                            userPage.getSize(),
                            userPage.getTotalElements(),
                            userPage.getTotalPages(),
                            userPage.isLast()
                    );
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<UserResponseDTO> getUserById(UUID id) {
        return Mono.fromCallable(() -> userRepository.findByIdAndDeletedAtIsNull(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalUser -> Mono.justOrEmpty(optionalUser))
                .map(this::mapToUserResponseDTO)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Usuário não encontrado com id: " + id)));
    }

//    @Override
//    public Mono<UserResponseDTO> updateUser(UUID id, UserRequestDTO userRequest) {
//        return Mono.fromCallable(() -> userRepository.findById(id))
//                .subscribeOn(Schedulers.boundedElastic())
//                .flatMap(optionalUser -> Mono.justOrEmpty(optionalUser))
//                .flatMap(user -> {
//                    // Se o username foi alterado, verifica a unicidade
//                    if (!user.getUsername().equals(userRequest.getUsername())) {
//                        return Mono.<Boolean>fromCallable(() -> userRepository.findByUsernameAndClientId(userRequest.getUsername(), userRequest.getClientId()).isPresent())
//                                .subscribeOn(Schedulers.boundedElastic())
//                                .flatMap(exists -> {
//                                    if (exists) {
//                                        return Mono.<UserResponseDTO>error(new DuplicateUsernameException("Username '" + userRequest.getUsername() + "' já existe para este cliente."));
//                                    }
//                                    return Mono.just(user);
//                                });
//                    }
//                    return Mono.just(user);
//                })
//                .flatMap(user -> {
//                    // Atualiza os campos
//
//                    return Mono.fromCallable(() -> clientRepository.findById(userRequest.getClientId()))
//                            .subscribeOn(Schedulers.boundedElastic())
//                            .flatMap(optionalClient -> Mono.justOrEmpty(optionalClient))
//                            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Cliente não encontrado com id: " + userRequest.getClientId())))
//                            .flatMap(client -> {
//                                return Mono.<UserEntity>fromCallable(() -> userRepository.save(user))
//                                        .subscribeOn(Schedulers.boundedElastic())
//                                        .map(this::mapToUserResponseDTO);
//                            });
//                })
//                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Usuário não encontrado com id: " + id)));
//    }

    @Override
    public Mono<Void> deleteUser(UUID id) {
        return Mono.fromCallable(() -> userRepository.findByIdAndDeletedAtIsNull(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalUser -> optionalUser
                        .map(user -> {
                            user.setDeletedAt(LocalDateTime.now());
                            return Mono.<UserEntity>fromCallable(() -> userRepository.save(user))
                                    .subscribeOn(Schedulers.boundedElastic())
                                    .then();
                        })
                        .orElse(Mono.error(new ResourceNotFoundException("Usuário não encontrado com id: " + id)))
                );
    }

    // ### Métodos de Mapeamento ###

    private UserResponseDTO mapToUserResponseDTO(UserEntity user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getApiKey(),
                new ClientDTO(
                        user.getClient().getId(),
                        user.getClient().getName(),
                        user.getClient().getRole()
                )
        );
    }

    // Classe de Exceção Personalizada
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}
