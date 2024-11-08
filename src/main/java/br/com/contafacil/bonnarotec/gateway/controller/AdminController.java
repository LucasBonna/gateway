package br.com.contafacil.bonnarotec.gateway.controller;

import br.com.contafacil.bonnarotec.gateway.domain.PagedResponseDTO;
import br.com.contafacil.bonnarotec.gateway.domain.client.ClientRequestDTO;
import br.com.contafacil.bonnarotec.gateway.domain.client.ClientResponseDTO;
import br.com.contafacil.bonnarotec.gateway.domain.user.UserRequestDTO;
import br.com.contafacil.bonnarotec.gateway.domain.user.UserResponseDTO;
import br.com.contafacil.bonnarotec.gateway.service.ClientService;
import br.com.contafacil.bonnarotec.gateway.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Rotas de disponiveis apenas para usuarios Admin")
public class AdminController {

    private final ClientService clientService;
    private final UserService userService;

    @Autowired
    public AdminController(UserService userService, ClientService clientService) {
        this.userService = userService;
        this.clientService = clientService;
    }

    @Operation(summary = "Criar Cliente", description = "Cria um novo cliente no sistema", responses = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content)
    })
    @PostMapping("/clients")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ClientResponseDTO> createClient(@RequestBody ClientRequestDTO clientRequest) {
        return clientService.createClient(clientRequest);
    }

    @GetMapping("/clients")
    public Mono<PagedResponseDTO<ClientResponseDTO>> getAllClients(
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Número da página (zero-based)", example = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Tamanho da página", example = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") @Parameter(description = "Campo para ordenar", example = "name") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") @Parameter(description = "Direção da ordenação (asc ou desc)", example = "asc") String sortDir
    ) {
        if (page < 0) {
            return Mono.error(new IllegalArgumentException("O número da página não pode ser negativo"));
        }
        if (size <= 0 || size > 100) {
            return Mono.error(new IllegalArgumentException("O tamanho da página deve estar entre 1 e 100"));
        }
        return clientService.getAllClients(page, size, sortBy, sortDir);
    }

    @Operation(summary = "Obter Cliente por ID", description = "Retorna um cliente específico pelo ID", responses = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content)
    })
    public Mono<ClientResponseDTO> getClientById(
            @Parameter(description = "ID do cliente", required = true, example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
            @PathVariable UUID id) {
        return clientService.getClientById(id);
    }

    @Operation(summary = "Atualizar Cliente", description = "Atualiza os detalhes de um cliente existente", responses = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content)
    })
    @PutMapping("/clients/{id}")
    public Mono<ClientResponseDTO> updateClient(
            @Parameter(description = "ID do cliente a ser atualizado", required = true, example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
            @PathVariable UUID id,
            @Valid @RequestBody ClientRequestDTO clientRequest) {
        return clientService.updateClient(id, clientRequest);
    }

    @Operation(summary = "Deletar Cliente", description = "Remove um cliente do sistema", responses = {
            @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content)
    })
    @DeleteMapping("/clients/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteClient(
            @Parameter(description = "ID do cliente a ser deletado", required = true, example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
            @PathVariable UUID id) {
        return clientService.deleteClient(id);
    }

    @Operation(summary = "Criar Usuário", description = "Cria um novo usuário no sistema", responses = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content)
    })
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponseDTO> createUser(
            @Valid @RequestBody UserRequestDTO userRequest) {
        return userService.createUser(userRequest);
    }

    @Operation(summary = "Listar Todos os Usuários", description = "Retorna uma lista paginada de todos os usuários", responses = {
            @ApiResponse(responseCode = "200", description = "Lista paginada de usuários", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagedResponseDTO.class)))
    })
    @GetMapping("/users")
    public Mono<PagedResponseDTO<UserResponseDTO>> getAllUsers(
            @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "Número da página (zero-based)", example = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "Tamanho da página", example = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "username") @Parameter(description = "Campo para ordenar", example = "username") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") @Parameter(description = "Direção da ordenação (asc ou desc)", example = "asc") String sortDir,
            @RequestParam(value = "clientId", required = false) UUID clientId
    ) {
        if (page < 0) {
            return Mono.error(new IllegalArgumentException("O número da página não pode ser negativo"));
        }
        if (size <= 0 || size > 100) {
            return Mono.error(new IllegalArgumentException("O tamanho da página deve estar entre 1 e 100"));
        }
        return userService.getAllUsers(page, size, sortBy, sortDir, clientId);
    }

    @Operation(summary = "Obter Usuário por ID", description = "Retorna um usuário específico pelo ID", responses = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @GetMapping("/users/{id}")
    public Mono<UserResponseDTO> getUserById(
            @Parameter(description = "ID do usuário", required = true, example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
            @PathVariable UUID id) {
        return userService.getUserById(id);
    }

//    @Operation(summary = "Atualizar Usuário", description = "Atualiza os detalhes de um usuário existente", responses = {
//            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
//            @ApiResponse(responseCode = "404", description = "Usuário ou Cliente não encontrado", content = @Content),
//            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content)
//    })
//    @PutMapping("/users/{id}")
//    public Mono<UserResponseDTO> updateUser(
//            @Parameter(description = "ID do usuário a ser atualizado", required = true, example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
//            @PathVariable UUID id,
//            @Valid @RequestBody UserRequestDTO userRequest) {
//        return userService.updateUser(id, userRequest);
//    }

    @Operation(summary = "Deletar Usuário", description = "Remove um usuário do sistema", responses = {
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(
            @Parameter(description = "ID do usuário a ser deletado", required = true, example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
            @PathVariable UUID id) {
        return userService.deleteUser(id);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return Mono.just(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    static class ErrorResponse {
        private int status;
        private String message;

        public ErrorResponse() {}

        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }


        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}
