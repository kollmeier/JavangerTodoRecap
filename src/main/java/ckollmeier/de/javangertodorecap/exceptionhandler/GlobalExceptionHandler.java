package ckollmeier.de.javangertodorecap.exceptionhandler;

import ckollmeier.de.javangertodorecap.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Catches all unhandled exceptions and returns an ErrorDTO with the exception details.
     *
     * @param exception The exception that was thrown.
     * @return An ErrorDTO containing the exception's class name, message, and HTTP status.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO catchAll(final Exception exception) {
        return new ErrorDTO(
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.name()
        );
    }
}
