package ckollmeier.de.javangertodorecap.dto;

public record OrthographyItemDTO(
        int textIndex,
        String originalText,
        String correctedText
) {
}
