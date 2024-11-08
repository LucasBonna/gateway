package br.com.contafacil.bonnarotec.gateway.domain.user;

import br.com.contafacil.shared.bonnarotec.toolslib.domain.client.ClientDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para resposta do usuario")
public class UserResponseDTO {

    @Schema(description = "ID único do usuário", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    private UUID id;

    @Schema(description = "Nome de usuário", example = "usuario123")
    private String username;

    @Schema(description = "Chave API do usuário", example = "chave-api-exemplo")
    private String apiKey;

    @Schema(description = "Informações do cliente associado")
    private ClientDTO client;
}
