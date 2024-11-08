package br.com.contafacil.bonnarotec.gateway.exception;

import br.com.contafacil.bonnarotec.gateway.service.implementation.UserServiceImpl.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ResourceNotFoundException.class})
    public Mono<ErrorResponse> handleNotFound(RuntimeException ex) {
        return Mono.just(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(WrongCredentialsException.class)
    public Mono<ErrorResponse> handleWrongCredentials(WrongCredentialsException ex) {
        return Mono.just(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateUsernameException.class)
    public Mono<ErrorResponse> handleDuplicateUsername(DuplicateUsernameException ex) {
        return Mono.just(new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    // Você pode adicionar handlers para outras exceções conforme necessário

    @Schema(description = "Resposta de Erro")
    static class ErrorResponse {
        @Schema(description = "Código de status HTTP", example = "404")
        private int status;

        @Schema(description = "Mensagem de erro", example = "Cliente não encontrado com id: ...")
        private String message;

        public ErrorResponse() {}

        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
        }

        // Getters e Setters

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
}
