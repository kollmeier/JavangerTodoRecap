package ckollmeier.de.javangertodorecap.converter;

import ckollmeier.de.javangertodorecap.entity.Todo;
import ckollmeier.de.javangertodorecap.dto.TodoInputDTO;
import ckollmeier.de.javangertodorecap.enums.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TodoConverterTest {
    @Test
    void convert_shouldConvertTodoInputDTOToTodo() {
        // Given
        String statusString = "OPEN";
        String description = "Test Todo";

        TodoInputDTO todoInputDTO = new TodoInputDTO(statusString, description);
        Todo expectedDTO = new Todo(null, Status.valueOf(statusString), description, null);

        // When
        Todo actualDTO = TodoConverter.convert(todoInputDTO);

        // Then
        assertEquals(expectedDTO, actualDTO);
    }

    @Test
    void convert_shouldThrowExceptionForNullInput() {
        // Given
        TodoInputDTO todoInputDTO = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> TodoConverter.convert(todoInputDTO));
    }

}