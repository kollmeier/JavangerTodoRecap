package ckollmeier.de.javangertodorecap.converter;

import ckollmeier.de.javangertodorecap.dto.TodoDTO;
import ckollmeier.de.javangertodorecap.entity.Todo;
import lombok.NonNull;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Util-Klasse zum Konvertieren in ein TodoDTO-Objekt.
 */
public final class TodoDTOConverter {
    private TodoDTOConverter() {
        // Util-Klasse
        throw new IllegalStateException("Utility class");
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault());

    /**
     * Konvertiert ein @{link Todo} in ein @{link TodoDTO}.
     * @param todo zu konvertierendes Todo
     * @return konvertiertes TodoDTO
     */
    public static TodoDTO convert(final @NonNull Todo todo) {
        return new TodoDTO(todo.id(), todo.status().toString(), todo.description(), formatter.format(todo.createdAt()));
    }

    /**
     * Konvertiert eine Liste von @{link Todo}s in eine Liste von @{link TodoDTO}s.
     * @param todos Liste von Todos
     * @return Liste von konvertierten TodoDTOs
     */
    public static List<TodoDTO> convert(final @NonNull List<Todo> todos) {
        return todos.stream()
                .map(TodoDTOConverter::convert)
                .toList();
    }

}
