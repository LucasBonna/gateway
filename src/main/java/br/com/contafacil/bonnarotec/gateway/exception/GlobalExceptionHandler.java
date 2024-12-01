package br.com.contafacil.bonnarotec.gateway.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return Mono.just(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateUsernameException.class)
    public Mono<ErrorResponse> handleDuplicateUsername(DuplicateUsernameException ex) {
        return Mono.just(new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidUserException.class)
    public Mono<ErrorResponse> handleInvalidUser(InvalidUserException ex) {
        return Mono.just(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public Mono<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        return Mono.just(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(WrongCredentialsException.class)
    public Mono<ErrorResponse> handleWrongCredentials(WrongCredentialsException ex) {
        return Mono.just(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Mono<ErrorResponse> handleGenericError(Exception ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("UnauthorizedException")) {
            return Mono.just(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), 
                ex.getMessage().substring(ex.getMessage().indexOf(":") + 2)));
        }
        return Mono.just(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
            "An unexpected error occurred: " + ex.getMessage()));
    }

    @Schema(description = "Error response")
    record ErrorResponse(
        @Schema(description = "HTTP status code") int status,
        @Schema(description = "Error message") String message
    ) {}
}
