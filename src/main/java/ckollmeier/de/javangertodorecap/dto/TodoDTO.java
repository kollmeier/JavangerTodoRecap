package ckollmeier.de.javangertodorecap.dto;

public record TodoDTO(
        String id,
        String status,
        String description,
        String createdAt
) {
}
