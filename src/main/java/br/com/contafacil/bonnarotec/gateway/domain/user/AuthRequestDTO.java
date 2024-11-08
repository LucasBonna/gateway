package br.com.contafacil.bonnarotec.gateway.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de Autenticação")
public record AuthRequestDTO(
        @Schema(description = "Nome de usuário", example = "meu_user")
        String username,

        @Schema(description = "Senha do usuário", example = "minha_senha")
        String password
) {
}
