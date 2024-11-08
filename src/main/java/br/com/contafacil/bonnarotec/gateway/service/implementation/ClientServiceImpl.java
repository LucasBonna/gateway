package br.com.contafacil.bonnarotec.gateway.service.implementation;

import br.com.contafacil.bonnarotec.gateway.domain.PagedResponseDTO;
import br.com.contafacil.bonnarotec.gateway.domain.client.ClientRepository;
import br.com.contafacil.bonnarotec.gateway.domain.client.ClientRequestDTO;
import br.com.contafacil.bonnarotec.gateway.domain.client.ClientResponseDTO;
import br.com.contafacil.bonnarotec.gateway.service.ClientService;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.client.ClientEntity;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.client.ClientRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Mono<ClientResponseDTO> createClient(ClientRequestDTO clientRequest) {
        ClientEntity client = new ClientEntity();
        client.setName(clientRequest.name());
        client.setRole(ClientRole.valueOf(clientRequest.role().toString()));

        return Mono.fromCallable(() -> clientRepository.save(client))
                .map(this::mapToClientResponseDTO);
    }

    @Override
    public Mono<PagedResponseDTO<ClientResponseDTO>> getAllClients(int page, int size, String sortBy, String sortDir) {
        return Mono.fromCallable(() -> {
                    Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
                    Pageable pageable = PageRequest.of(page, size, sort);
                    Page<ClientEntity> clientPage = clientRepository.findAll(pageable);

                    List<ClientResponseDTO> content = clientPage.getContent()
                            .stream()
                            .map(this::mapToClientResponseDTO)
                            .collect(Collectors.toList());

                    return new PagedResponseDTO<>(
                            content,
                            clientPage.getNumber(),
                            clientPage.getSize(),
                            clientPage.getTotalElements(),
                            clientPage.getTotalPages(),
                            clientPage.isLast()
                    );
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ClientResponseDTO> getClientById(UUID id) {
        return Mono.fromCallable(() -> clientRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalClient -> {
                    if (optionalClient.isPresent()) {
                        ClientResponseDTO dto = mapToClientResponseDTO(optionalClient.get());
                        return Mono.just(dto);
                    } else {
                        return Mono.error(new ResourceNotFoundException("Cliente não encontrado com id: " + id));
                    }
                });
    }

    @Override
    public Mono<ClientResponseDTO> updateClient(UUID id, ClientRequestDTO clientRequest) {
        return Mono.fromCallable(() -> clientRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalClient -> optionalClient
                        .map(client -> {
                            client.setName(clientRequest.name());
                            client.setRole(ClientRole.valueOf(clientRequest.role().toString()));
                            return Mono.fromCallable(() -> clientRepository.save(client))
                                    .subscribeOn(Schedulers.boundedElastic());
                        })
                        .orElseGet(() -> Mono.error(new ResourceNotFoundException("Cliente não encontrado com id: " + id))))
                .map(this::mapToClientResponseDTO);
    }


    @Override
    public Mono<Void> deleteClient(UUID id) {
        return Mono.fromCallable(() -> clientRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalClient -> optionalClient
                        .map(client -> {
                            clientRepository.delete(client);
                            return Mono.<Void>empty();
                        })
                        .orElseGet(() -> Mono.error(new ResourceNotFoundException("Cliente não encontrado com id: " + id))))
                .then();
    }

    // ### Métodos de Mapeamento ###

    private ClientResponseDTO mapToClientResponseDTO(ClientEntity client) {
        return new ClientResponseDTO(
                client.getId(),
                client.getName(),
                client.getRole()
        );
    }

    // Classe de Exceção Personalizada
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}
