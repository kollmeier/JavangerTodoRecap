package ckollmeier.de.javangertodorecap.exceptionhandler;

import ckollmeier.de.javangertodorecap.dto.ErrorDTO;
import ckollmeier.de.javangertodorecap.dto.SpellingValidationDTO;
import ckollmeier.de.javangertodorecap.dto.ValidationErrorDTO;
import ckollmeier.de.javangertodorecap.exception.NotFoundException;
import ckollmeier.de.javangertodorecap.exception.SpellingValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles the {@link SpellingValidationException} and returns a {@link ValidationErrorDTO}
     * containing details about the validation errors.
     *
     * @param exception The {@link SpellingValidationException} that was thrown.
     *                  Contains information about the spelling validation errors.
     * @return A {@link ValidationErrorDTO} containing the exception's class name, message,
     *         HTTP status, and details about the spelling validation errors.
     */
    @ExceptionHandler(SpellingValidationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ValidationErrorDTO catchSpellingValidationException(final SpellingValidationException exception) {
        return new ValidationErrorDTO(
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                HttpStatus.UNPROCESSABLE_ENTITY.name(),
                exception.getSpellingValidation()
        );
    }

    /**
     * Catches a NotFoundException and returns an ErrorDTO with the exception details.
     *
     * @param exception The exception that was thrown.
     * @return An ErrorDTO containing the exception's class name, message, and HTTP status.
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO catchNullPointerException(final NotFoundException exception) {
        return new ErrorDTO(
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                HttpStatus.NOT_FOUND.name()
        );
    }

    /**
     * Catches a NullPointerException and returns an ErrorDTO with the exception details.
     *
     * @param exception The exception that was thrown.
     * @return An ErrorDTO containing the exception's class name, message, and HTTP status.
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO catchNullPointerException(final NullPointerException exception) {
        return new ErrorDTO(
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                HttpStatus.BAD_REQUEST.name()
        );
    }

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
