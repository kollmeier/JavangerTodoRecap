package ckollmeier.de.javangertodorecap.exception;

import ckollmeier.de.javangertodorecap.dto.SpellingValidationDTO;
import lombok.Getter;

@Getter
public class SpellingValidationException extends RuntimeException {
    final SpellingValidationDTO spellingValidation;
    public SpellingValidationException(final String message, final SpellingValidationDTO spellingValidation) {
        super(message);
        this.spellingValidation = spellingValidation;
    }

    public SpellingValidationException(final String message, final Throwable cause, final SpellingValidationDTO spellingValidation) {
        super(message, cause);
        this.spellingValidation = spellingValidation;
    }
}
