package ckollmeier.de.javangertodorecap.dto;

public record ValidationErrorDTO(
                                 String error,
                                 String message,
                                 String status,
                                 SpellingValidationDTO spellingValidation
) {
}