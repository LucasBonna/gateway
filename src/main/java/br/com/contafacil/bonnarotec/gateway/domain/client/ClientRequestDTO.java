package br.com.contafacil.bonnarotec.gateway.domain.client;

import br.com.contafacil.shared.bonnarotec.toolslib.domain.client.ClientRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;


@Schema(description = "DTO para requisição de Cliente")
public record ClientRequestDTO(
        @Schema(description = "Nome do cliente", example = "Cliente Exemplo", required = true)
        @NotBlank(message = "Nome é obrigatório")
        String name,

        @Schema(description = "Papel do cliente", example = "ADMIN", required = true)
        @NotNull(message = "Papel é obrigatório")
        ClientRole role
) {}
