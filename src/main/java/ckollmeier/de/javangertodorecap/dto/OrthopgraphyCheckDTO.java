package ckollmeier.de.javangertodorecap.dto;

import java.util.List;

public record OrthopgraphyCheckDTO(
        int errorCount,
        List<OrthographyItemDTO> errors
) {
}
