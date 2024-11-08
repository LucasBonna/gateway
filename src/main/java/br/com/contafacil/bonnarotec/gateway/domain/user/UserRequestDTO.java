package br.com.contafacil.bonnarotec.gateway.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para requisicao de usuario")
public class UserRequestDTO {

        @Schema(description = "Nome de usuario", example = "usuario123", required = true)
        @NotBlank(message = "Username é obrigatório")
        private String username;

        @Schema(description = "Senha do usuário", example = "senhaSegura", required = true)
        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        private String password;

        @Schema(description = "ID do cliente associado", example = "d290f1ee-6c54-4b01-90e6-d701748f0851", required = true)
        @NotNull(message = "Client ID é obrigatório")
        private UUID clientId;
}
