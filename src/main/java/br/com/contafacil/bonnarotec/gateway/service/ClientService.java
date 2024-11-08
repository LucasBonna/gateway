package br.com.contafacil.bonnarotec.gateway.service;

import br.com.contafacil.bonnarotec.gateway.domain.PagedResponseDTO;
import br.com.contafacil.bonnarotec.gateway.domain.client.ClientRequestDTO;
import br.com.contafacil.bonnarotec.gateway.domain.client.ClientResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ClientService {
    Mono<ClientResponseDTO> createClient(ClientRequestDTO clientRequest);
    Mono<PagedResponseDTO<ClientResponseDTO>> getAllClients(int page, int size, String sortBy, String sortDir);
    Mono<ClientResponseDTO> getClientById(UUID id);
    Mono<ClientResponseDTO> updateClient(UUID id, ClientRequestDTO clientRequest);
    Mono<Void> deleteClient(UUID id);
}
