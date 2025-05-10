package ckollmeier.de.javangertodorecap.converter;

import ckollmeier.de.javangertodorecap.dto.TodoDTO;
import ckollmeier.de.javangertodorecap.entity.Todo;
import ckollmeier.de.javangertodorecap.enums.Status;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TodoDTOConverterTest {

    @Test
    void convert_shouldConvertTodoToTodoDTO() {
        // Given
        String id = UUID.randomUUID().toString();
        String statusString = "OPEN";
        String description = "Test Todo";

        Todo todo = new Todo(id, Status.valueOf(statusString), description, null);
        TodoDTO expectedDTO = new TodoDTO(id, statusString, description, null);

        // When
        TodoDTO actualDTO = TodoDTOConverter.convert(todo);

        // Then
        assertEquals(expectedDTO, actualDTO);
    }

    @Test
    void convert_shouldThrowExceptionForNullInput() {
        // Given
        Todo todo = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> TodoDTOConverter.convert(todo));
    }

    @Test
    void convert_shouldConvertListOfTodosToListOfTodoDTOs() {
        // Given
        String id1 = UUID.randomUUID().toString();
        String statusString = "OPEN";
        String description1 = "Test Todo 1";

        Todo todo1 = new Todo(id1, Status.valueOf(statusString), description1, null);
        TodoDTO expectedDTO1 = new TodoDTO(id1, statusString, description1, null);

        String id2 = UUID.randomUUID().toString();
        String description2 = "Test Todo 2";

        Todo todo2 = new Todo(id2, Status.valueOf(statusString), description2, null);
        TodoDTO expectedDTO2 = new TodoDTO(id2, statusString, description2, null);

        // When
        List<TodoDTO> actualDTOs = TodoDTOConverter.convert(List.of(todo1, todo2));

        // Then
        assertEquals(2, actualDTOs.size());
        assertTrue(actualDTOs.contains(expectedDTO1));
        assertTrue(actualDTOs.contains(expectedDTO2));
    }

    @Test
    void convert_shouldThrowExceptionForNullInputList() {
        // Given
        List<Todo> todos = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> TodoDTOConverter.convert(todos));
    }
}