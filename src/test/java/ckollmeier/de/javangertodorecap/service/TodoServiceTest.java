package ckollmeier.de.javangertodorecap.service;

import ckollmeier.de.javangertodorecap.dto.TodoDTO;
import ckollmeier.de.javangertodorecap.entity.Todo;
import ckollmeier.de.javangertodorecap.enums.Status;
import ckollmeier.de.javangertodorecap.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TodoServiceTest {

    private TodoService todoService;
    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        todoRepository = Mockito.mock(TodoRepository.class);
        todoService = new TodoService(
                todoRepository
        );
    }

    @Test
    void getAllTodos_shouldReturnTodosAsDTOs() {
        // Given
        List<Todo> todos = new ArrayList<>();
        String id1 = UUID.randomUUID().toString();
        String statusString = "OPEN";
        String description1 = "Test Todo 1";

        Todo todo1 = new Todo(id1, Status.valueOf(statusString), description1);
        TodoDTO expectedDTO1 = new TodoDTO(id1, statusString, description1);
        todos.add(todo1);

        String id2 = UUID.randomUUID().toString();
        String description2 = "Test Todo 2";

        Todo todo2 = new Todo(id2, Status.valueOf(statusString), description2);
        TodoDTO expectedDTO2 = new TodoDTO(id2, statusString, description2);
        todos.add(todo2);

        Mockito.when(todoRepository.findAll()).thenReturn(todos);

        // When
        List<TodoDTO> actualDTOs = todoService.getAllTodos();

        // Then
        assertEquals(2, actualDTOs.size());
        assertTrue(actualDTOs.contains(expectedDTO1));
        assertTrue(actualDTOs.contains(expectedDTO2));
    }

    @Test
    void getAllTodos_shouldReturnEmptyListForNoTodos() {
        // Given
        Mockito.when(todoRepository.findAll()).thenReturn(new ArrayList<>());

        // When
        List<TodoDTO> actualDTOs = todoService.getAllTodos();

        // Then
        assertTrue(actualDTOs.isEmpty());
    }
}