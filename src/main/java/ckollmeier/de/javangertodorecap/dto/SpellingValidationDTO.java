package ckollmeier.de.javangertodorecap.dto;

import java.util.List;

public record SpellingValidationDTO(
        int errorCount,
        List<SpellingValidationItemDTO> errors
) {
}
