package br.com.contafacil.bonnarotec.gateway.domain.client;

import br.com.contafacil.shared.bonnarotec.toolslib.domain.client.ClientRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para resposta do cliente")
public class ClientResponseDTO {

    @Schema(description = "Id unico do cliente", example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
    private UUID id;

    @Schema(description = "Nome do cliente", example = "Cliente de exemplo")
    private String nome;

    @Schema(description = "Papel do cliente", example = "ADMIN")
    private ClientRole role;
}
