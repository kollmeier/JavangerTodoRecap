package ckollmeier.de.javangertodorecap.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record SpellingValidationItemDTO(
        @JsonPropertyDescription("positions of passage or word to be changed")
        int textPassagePosition,
        @JsonPropertyDescription("passage or word to be changed")
        String originalTextPassage,
        @JsonPropertyDescription("passage or word to change to")
        String correctedTextPassage,
        @JsonPropertyDescription("Full text with former corrections already applied")
        String fullText,
        @JsonPropertyDescription("Full text with former corrections and actual correction already applied")
        String fullCorrectedText,
        @JsonPropertyDescription("Description of the mistake")
        String description
) {
}
