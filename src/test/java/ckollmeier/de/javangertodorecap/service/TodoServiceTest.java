package ckollmeier.de.javangertodorecap.service;

import ckollmeier.de.javangertodorecap.dto.TodoDTO;
import ckollmeier.de.javangertodorecap.dto.TodoInputDTO;
import ckollmeier.de.javangertodorecap.entity.Todo;
import ckollmeier.de.javangertodorecap.enums.Status;
import ckollmeier.de.javangertodorecap.exception.NotFoundException;
import ckollmeier.de.javangertodorecap.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TodoServiceTest {

    private TodoService todoService;
    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        todoRepository = Mockito.mock(TodoRepository.class);
        todoService = new TodoService(todoRepository, Mockito.mock(HistoryService.class), Mockito.mock(ChatGPTService.class));
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

    @Test
    void addTodo_shouldAddTodoAndReturnDTO() {
        // Given
        String statusString = "OPEN";
        String description = "Test Todo";

        TodoInputDTO todoInputDTO = new TodoInputDTO(statusString, description);
        Todo todo = new Todo(null, Status.valueOf(statusString), description);
        TodoDTO expectedDTO = new TodoDTO(null, statusString, description);
        Mockito.when(todoRepository.save(Mockito.any(Todo.class))).thenReturn(todo);

        // When
        TodoDTO actualDTO = todoService.addTodo(todoInputDTO);

        // Then
        assertEquals(expectedDTO.status(), actualDTO.status());
        assertEquals(expectedDTO.description(), actualDTO.description());
        assertNotNull(actualDTO.id());

        Mockito.verify(todoRepository, Mockito.times(1)).save(Mockito.any(Todo.class));
    }

    @Test
    void addTodo_shouldThrowExceptionForNullInput() {
        // Given
        TodoInputDTO todoInputDTO = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> todoService.addTodo(todoInputDTO));
    }

    @Test
    void getTodoById_shouldReturnExistingTodoAsDTO() {
        // Given
        String id = UUID.randomUUID().toString();
        String statusString = "OPEN";
        String description = "Test Todo";

        Todo todo = new Todo(id, Status.valueOf(statusString), description);
        TodoDTO expectedDTO = new TodoDTO(id, statusString, description);
        Mockito.when(todoRepository.findById(id)).thenReturn(java.util.Optional.of(todo));

        // When
        TodoDTO actualDTO = todoService.getTodoById(id);

        // Then
        assertEquals(expectedDTO.id(), actualDTO.id());
        assertEquals(expectedDTO.status(), actualDTO.status());
        assertEquals(expectedDTO.description(), actualDTO.description());

        Mockito.verify(todoRepository, Mockito.times(1)).findById(id);
    }

    @Test
    void getTodoById_shouldThrowExceptionForNonExistingTodo() {
        // Given
        String id = UUID.randomUUID().toString();
        Mockito.when(todoRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> todoService.getTodoById(id));
    }

    @Test
    void updateTodo_shouldUpdateTodoAndReturnDTO() {
        // Given
        String id = UUID.randomUUID().toString();
        String previousStatusString = "IN_PROGRESS";
        String previousDescription = "Previous Todo";
        String statusString = "OPEN";
        String description = "Test Todo";

        TodoInputDTO todoInputDTO = new TodoInputDTO(statusString, description);
        Todo previousTodo = new Todo(id, Status.valueOf(previousStatusString), previousDescription);
        Todo todo = new Todo(id, Status.valueOf(statusString), description);
        TodoDTO expectedDTO = new TodoDTO(id, statusString, description);
        Mockito.when(todoRepository.findById(id)).thenReturn(Optional.of(previousTodo));
        Mockito.when(todoRepository.save(Mockito.any(Todo.class))).thenReturn(todo);

        // When
        TodoDTO actualDTO = todoService.updateTodo(id, todoInputDTO);

        // Then
        assertEquals(expectedDTO.id(), actualDTO.id());
        assertEquals(expectedDTO.status(), actualDTO.status());
        assertEquals(expectedDTO.description(), actualDTO.description());

        Mockito.verify(todoRepository, Mockito.times(1)).findById(id);
        Mockito.verify(todoRepository, Mockito.times(1)).save(Mockito.any(Todo.class));
    }

    @Test
    void updateTodo_shouldThrowExceptionForNonExistingTodo() {
        // Given
        String id = UUID.randomUUID().toString();
        String statusString = "OPEN";
        String description = "Test Todo";
        TodoInputDTO todoInputDTO = new TodoInputDTO(statusString, description);
        Mockito.when(todoRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThrows(NotFoundException.class, () -> todoService.updateTodo(id, todoInputDTO));
    }

    @Test
    void updateTodo_shouldThrowExceptionForNullInput() {
        // Given
        String id = UUID.randomUUID().toString();
        TodoInputDTO todoInputDTO = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> todoService.updateTodo(id, todoInputDTO));
    }

    @Test
    void updateTodo_shouldThrowExceptionForNullId() {
        // Given
        String id = null;
        TodoInputDTO todoInputDTO = new TodoInputDTO("OPEN", "Test Todo");

        // When & Then
        assertThrows(NullPointerException.class, () -> todoService.updateTodo(id, todoInputDTO));
    }

    @Test
    void deleteTodo_shouldDeleteTodo() {
        // Given
        String id = UUID.randomUUID().toString();
        Mockito.when(todoRepository.findById(id)).thenReturn(Optional.of(new Todo(id, Status.OPEN, "Test Todo")));

        // When
        todoService.deleteTodo(id);

        // Then
        Mockito.verify(todoRepository, Mockito.times(1)).findById(id);
        Mockito.verify(todoRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    void deleteTodo_shouldThrowExceptionForNonExistingTodo() {
        // Given
        String id = UUID.randomUUID().toString();
        Mockito.when(todoRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThrows(NotFoundException.class, () -> todoService.deleteTodo(id));
    }
}