package ckollmeier.de.javangertodorecap.dto;

public record ErrorDTO(
        String error,
        String message,
        String status
) {
}
