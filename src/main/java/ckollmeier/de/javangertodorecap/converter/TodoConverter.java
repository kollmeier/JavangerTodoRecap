package ckollmeier.de.javangertodorecap.converter;

import ckollmeier.de.javangertodorecap.dto.TodoInputDTO;
import ckollmeier.de.javangertodorecap.entity.Todo;
import ckollmeier.de.javangertodorecap.enums.Status;
import lombok.NonNull;

/**
 * Util-Klasse zum Konvertieren in ein @{link Todo}-Objekt.
 */
public final class TodoConverter {
    private TodoConverter() {
        // Util-Klasse
        throw new IllegalStateException("Utility class");
    }

    /**
     * Konvertiert ein @{link TodoInputDTO} in ein @{link Todo}.
     * @param todoInputDTO zu konvertierendes TodoInputDTO
     * @return konvertiertes Todo
     */
    public static Todo convert(final @NonNull TodoInputDTO todoInputDTO) {
        return new Todo(null, Status.valueOf(todoInputDTO.status()), todoInputDTO.description());
    }
}
