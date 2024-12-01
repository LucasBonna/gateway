package br.com.contafacil.bonnarotec.gateway.util;

import br.com.contafacil.bonnarotec.gateway.exception.InvalidUserException;
import br.com.contafacil.bonnarotec.gateway.exception.UnauthorizedException;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.user.UserDTO;
import br.com.contafacil.shared.bonnarotec.toolslib.domain.user.UserRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class Utils {

    private final ObjectMapper objectMapper;

    public UserDTO getUserDTO(String userHeaderString) {
        try {
            return objectMapper.readValue(userHeaderString, UserDTO.class);
        } catch (JsonProcessingException e) {
            throw new InvalidUserException("Invalid user");
        }
    }

    public void validateAdminAccess(UserDTO user) {
        if (user.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("Only admin users can perform this operation");
        }
    }

    public void validateUserAccess(UserDTO currentUser, UUID targetUserId) {
        if (currentUser.getRole() != UserRole.ADMIN && !currentUser.getId().equals(targetUserId)) {
            throw new UnauthorizedException("You can only modify your own user data unless you are an admin");
        }
    }

    public void validateClientAccess(UserDTO currentUser, UUID clientId) {
        if (currentUser.getRole() != UserRole.ADMIN && !currentUser.getClient().getId().equals(clientId)) {
            throw new UnauthorizedException("You can only access data from your own client unless you are an admin");
        }
    }
}
