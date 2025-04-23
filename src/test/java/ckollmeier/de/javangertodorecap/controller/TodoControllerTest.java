package ckollmeier.de.javangertodorecap.controller;

import ckollmeier.de.javangertodorecap.dto.TodoInputDTO;
import ckollmeier.de.javangertodorecap.entity.HistoryEntry;
import ckollmeier.de.javangertodorecap.entity.Todo;
import ckollmeier.de.javangertodorecap.enums.Status;
import ckollmeier.de.javangertodorecap.repository.HistoryEntryRepository;
import ckollmeier.de.javangertodorecap.repository.TodoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.bson.json.JsonObject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private HistoryEntryRepository historyEntryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        todoRepository.deleteAll();
        historyEntryRepository.deleteAll();
    }

    @Test
    void getAllTodos_shouldReturnTodosAsJsonWithCorrectDTOs() throws Exception {
        // Given
        Todo todo1 = new Todo("id-1", Status.OPEN, "Test Todo 1");
        Todo todo2 = new Todo("id-2", Status.OPEN, "Test Todo 2");
        Todo todo3 = new Todo("id-3", Status.OPEN, "Test Todo 3");

        todoRepository.save(todo1);
        todoRepository.save(todo2);
        todoRepository.save(todo3);

        // When / Then
        mockMvc.perform(get("/api/todo"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                            {
                                "id": "id-1",
                                "status": "OPEN",
                                "description": "Test Todo 1"
                            },
                            {
                                "id": "id-2",
                                "status": "OPEN",
                                "description": "Test Todo 2"
                            },
                            {
                                "id": "id-3",
                                "status": "OPEN",
                                "description": "Test Todo 3"
                            }
                        ]
                """));
    }

    @Test
    void getAllTodos_shouldReturnEmptyListForNoTodos() throws Exception{
        // When / Then
        mockMvc.perform(get("/api/todo"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void addTodo_shouldAddTodoAndReturnDTO() throws Exception {
        // Given
        TodoInputDTO todoInputDTO = new TodoInputDTO("OPEN", "Test Todo");
        ObjectWriter objectWriter = new ObjectMapper().writer();
        JsonObject jsonTodoInputDTO = new JsonObject(objectWriter.writeValueAsString(todoInputDTO));

        // When / Then
        mockMvc.perform(post("/api/todo").contentType("application/json").content(jsonTodoInputDTO.getJson()))
                .andExpect(status().isCreated())
                .andExpect(content().json("""
                            {
                                "status": "OPEN",
                                "description": "Test Todo"
                            }
                        """))
                .andExpect(jsonPath("$.id").isNotEmpty());
        List<Todo> savedTodos = todoRepository.findAll();
        assertEquals(1, savedTodos.size());
        Todo savedTodo = savedTodos.getFirst();
        assertEquals(todoInputDTO.status(), savedTodo.status().toString());
        assertEquals(todoInputDTO.description(), savedTodo.description());
    }

    @Test
    void getTodoById_shouldReturnExistingTodoAsDTO() throws Exception {
        // Given
        Todo todo = new Todo("id-1", Status.OPEN, "Test Todo");
        todoRepository.save(todo);

        // When / Then
        mockMvc.perform(get("/api/todo/id-1"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "id": "id-1",
                            "status": "OPEN",
                            "description": "Test Todo"
                        }
                    """));
    }

    @Test
    void getTodoById_shouldReturn404ForNonExistingTodo() throws Exception {
        // Given
        Todo todo = new Todo("id-1", Status.OPEN, "Test Todo");
        todoRepository.save(todo);

        // When / Then
        mockMvc.perform(get("/api/todo/non-existing-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTodo_shouldUpdateTodoAndReturnDTO() throws Exception {
        // Given
        Todo todo = new Todo("id-1", Status.OPEN, "Test Todo");
        todoRepository.save(todo);

        TodoInputDTO todoInputDTO = new TodoInputDTO("IN_PROGRESS", "Updated Test Todo");
        ObjectWriter objectWriter = new ObjectMapper().writer();
        JsonObject jsonTodoInputDTO = new JsonObject(objectWriter.writeValueAsString(todoInputDTO));

        // When / Then
        mockMvc.perform(put("/api/todo/id-1").contentType("application/json").content(jsonTodoInputDTO.getJson()))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "id": "id-1",
                            "status": "IN_PROGRESS",
                            "description": "Updated Test Todo"
                        }
                    """));
        List<Todo> savedTodos = todoRepository.findAll();
        assertEquals(1, savedTodos.size());
        Todo savedTodo = savedTodos.getFirst();
        assertEquals(todoInputDTO.status(), savedTodo.status().toString());
        assertEquals(todoInputDTO.description(), savedTodo.description());
    }

    @Test
    void updateTodo_shouldReturn404ForNonExistingTodo() throws Exception {
        // Given
        Todo todo = new Todo("id-1", Status.OPEN, "Test Todo");
        todoRepository.save(todo);

        TodoInputDTO todoInputDTO = new TodoInputDTO("IN_PROGRESS", "Updated Test Todo");
        ObjectWriter objectWriter = new ObjectMapper().writer();
        JsonObject jsonTodoInputDTO = new JsonObject(objectWriter.writeValueAsString(todoInputDTO));

        // When / Then
        mockMvc.perform(put("/api/todo/non-existing-id").contentType("application/json").content(jsonTodoInputDTO.getJson()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTodo() throws Exception {
        // Given
        Todo todo = new Todo("id-1", Status.OPEN, "Test Todo");
        todoRepository.save(todo);

        // When / Then
        mockMvc.perform(delete("/api/todo/id-1"))
                .andExpect(status().isNoContent());
        List<Todo> savedTodos = todoRepository.findAll();
        assertEquals(0, savedTodos.size());
    }

    @Test
    void deleteTodo_shouldReturn404ForNonExistingTodo() throws Exception {
        // Given
        Todo todo = new Todo("id-1", Status.OPEN, "Test Todo");
        todoRepository.save(todo);

        // When / Then
        mockMvc.perform(delete("/api/todo/non-existing-id"))
                .andExpect(status().isNotFound());
    }

    // --- Tests für Undo ---
    @Nested
    @DisplayName("Undo Endpoint Tests")
    class UndoTests {

        @Test
        @DisplayName("POST /undo sollte CREATE rückgängig machen und 204 zurückgeben")
        void undoLastAction_shouldUndoCreateAndReturnNoContent() throws Exception {
            // Given: Ein Todo erstellen (erzeugt History-Eintrag)
            TodoInputDTO createDto = new TodoInputDTO("OPEN", "Undo Create Test");
            MvcResult createResult = mockMvc.perform(post("/api/todo")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isCreated())
                    .andReturn();
            String createdId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();
            Assertions.assertThat(todoRepository.existsById(createdId)).isTrue();
            assertThat(historyEntryRepository.count()).isEqualTo(1);

            // When: Undo aufrufen
            mockMvc.perform(post("/api/todo/undo"))
                    .andExpect(status().isNoContent());

            // Then: Todo sollte gelöscht sein, History-Eintrag als undone markiert
            Assertions.assertThat(todoRepository.existsById(createdId)).isFalse();
            Optional<HistoryEntry<?>> historyEntryOpt = historyEntryRepository.findFirstByEntityIdOrderByDoneAtDesc(createdId);
            Assertions.assertThat(historyEntryOpt).isPresent();
            Assertions.assertThat(historyEntryOpt.get().undoneAt()).isNotNull();
        }

        @Test
        @DisplayName("POST /undo sollte UPDATE rückgängig machen und 204 zurückgeben")
        void undoLastAction_shouldUndoUpdateAndReturnNoContent() throws Exception {
            // Given: Todo erstellen und dann updaten
            Todo initialTodo = todoRepository.save(new Todo(null, Status.OPEN, "Initial State"));
            String todoId = initialTodo.id();

            TodoInputDTO updateDto = new TodoInputDTO("DONE", "Updated State");
            mockMvc.perform(put("/api/todo/" + todoId)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isOk());

            Assertions.assertThat(todoRepository.findById(todoId).get().status()).isEqualTo(Status.DONE);
            assertThat(historyEntryRepository.count()).isEqualTo(1); // Nur Update wird historisiert

            // When: Undo aufrufen
            mockMvc.perform(post("/api/todo/undo"))
                    .andExpect(status().isNoContent());

            // Then: Todo sollte im initialen Zustand sein, History-Eintrag als undone markiert
            Optional<Todo> revertedTodoOpt = todoRepository.findById(todoId);
            Assertions.assertThat(revertedTodoOpt).isPresent();
            Assertions.assertThat(revertedTodoOpt.get().status()).isEqualTo(Status.OPEN); // Zurück zum initialen Status
            Assertions.assertThat(revertedTodoOpt.get().description()).isEqualTo("Initial State"); // Zurück zur initialen Beschreibung

            Optional<HistoryEntry<?>> historyEntryOpt = historyEntryRepository.findFirstByEntityIdOrderByDoneAtDesc(todoId);
            Assertions.assertThat(historyEntryOpt).isPresent();
            Assertions.assertThat(historyEntryOpt.get().undoneAt()).isNotNull();
        }

        @Test
        @DisplayName("POST /undo sollte DELETE rückgängig machen und 204 zurückgeben")
        void undoLastAction_shouldUndoDeleteAndReturnNoContent() throws Exception {
            // Given: Todo erstellen und dann löschen
            Todo todoToDelete = todoRepository.save(new Todo(null, Status.IN_PROGRESS, "To be deleted then restored"));
            String todoId = todoToDelete.id();

            mockMvc.perform(delete("/api/todo/" + todoId))
                    .andExpect(status().isNoContent());

            Assertions.assertThat(todoRepository.existsById(todoId)).isFalse();
            assertThat(historyEntryRepository.count()).isEqualTo(1); // Nur Delete wird historisiert

            // When: Undo aufrufen
            mockMvc.perform(post("/api/todo/undo"))
                    .andExpect(status().isNoContent());

            // Then: Todo sollte wieder existieren, History-Eintrag als undone markiert
            Optional<Todo> restoredTodoOpt = todoRepository.findById(todoId);
            Assertions.assertThat(restoredTodoOpt).isPresent();
            Assertions.assertThat(restoredTodoOpt.get().status()).isEqualTo(Status.IN_PROGRESS); // Ursprünglicher Zustand
            Assertions.assertThat(restoredTodoOpt.get().description()).isEqualTo("To be deleted then restored");

            Optional<HistoryEntry<?>> historyEntryOpt = historyEntryRepository.findFirstByEntityIdOrderByDoneAtDesc(todoId);
            Assertions.assertThat(historyEntryOpt).isPresent();
            Assertions.assertThat(historyEntryOpt.get().undoneAt()).isNotNull();
        }

        @Test
        @DisplayName("POST /undo sollte 204 zurückgeben, wenn nichts zum Rückgängigmachen da ist")
        void undoLastAction_shouldReturnNoContent_whenNothingToUndo() throws Exception {
            // Given: Leere Repositories (durch setUp)
            assertThat(todoRepository.count()).isZero();
            Assertions.assertThat(historyEntryRepository.count()).isZero();

            // When: Undo aufrufen
            mockMvc.perform(post("/api/todo/undo"))
                    .andExpect(status().isNoContent());

            // Then: Repositories sollten immer noch leer sein
            assertThat(todoRepository.count()).isZero();
            assertThat(historyEntryRepository.count()).isZero();
        }
    }

    // --- Tests für Redo ---
    @Nested
    @DisplayName("Redo Endpoint Tests")
    class RedoTests {

        @Test
        @DisplayName("POST /redo sollte CREATE wieder herstellen und 204 zurückgeben")
        void redoLastAction_shouldRedoCreateAndReturnNoContent() throws Exception {
            // Given: Ein Todo erstellen (erzeugt History-Eintrag)
            TodoInputDTO createDto = new TodoInputDTO("OPEN", "Undo Create Test");
            MvcResult createResult = mockMvc.perform(post("/api/todo")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isCreated())
                    .andReturn();
            String createdId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();
            Assertions.assertThat(todoRepository.existsById(createdId)).isTrue();
            assertThat(historyEntryRepository.count()).isEqualTo(1);
            mockMvc.perform(post("/api/todo/undo"))
                    .andExpect(status().isNoContent());

            // When: Redo aufrufen
            mockMvc.perform(post("/api/todo/redo"))
                    .andExpect(status().isNoContent());

            // Then: Todo sollte existieren, History-Eintrag als nicht undone markiert
            Assertions.assertThat(todoRepository.existsById(createdId)).isTrue();
            Optional<HistoryEntry<?>> historyEntryOpt = historyEntryRepository.findFirstByEntityIdOrderByDoneAtDesc(createdId);
            Assertions.assertThat(historyEntryOpt).isPresent();
            Assertions.assertThat(historyEntryOpt.get().undoneAt()).isNull();
        }

        @Test
        @DisplayName("POST /redo sollte UPDATE wiederherstellen und 204 zurückgeben")
        void redoLastAction_shouldRedoUpdateAndReturnNoContent() throws Exception {
            // Given: Todo erstellen und dann updaten
            Todo initialTodo = todoRepository.save(new Todo(null, Status.OPEN, "Initial State"));
            String todoId = initialTodo.id();

            TodoInputDTO updateDto = new TodoInputDTO("DONE", "Updated State");
            mockMvc.perform(put("/api/todo/" + todoId)
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isOk());

            Assertions.assertThat(todoRepository.findById(todoId).get().status()).isEqualTo(Status.DONE);
            assertThat(historyEntryRepository.count()).isEqualTo(1); // Nur Update wird historisiert
            mockMvc.perform(post("/api/todo/undo"))
                    .andExpect(status().isNoContent());

            // When: Undo aufrufen
            mockMvc.perform(post("/api/todo/redo"))
                    .andExpect(status().isNoContent());

            // Then: Todo sollte wieder im geänderten Zustand sein, History-Eintrag als nicht undone markiert
            Optional<Todo> redoneTodoOpt = todoRepository.findById(todoId);
            Assertions.assertThat(redoneTodoOpt).isPresent();
            Assertions.assertThat(redoneTodoOpt.get().status()).isEqualTo(Status.DONE); // Zurück zum geänderten Status
            Assertions.assertThat(redoneTodoOpt.get().description()).isEqualTo("Updated State"); // Zurück zur geänderten Beschreibung

            Optional<HistoryEntry<?>> historyEntryOpt = historyEntryRepository.findFirstByEntityIdOrderByDoneAtDesc(todoId);
            Assertions.assertThat(historyEntryOpt).isPresent();
            Assertions.assertThat(historyEntryOpt.get().undoneAt()).isNull();
        }

        @Test
        @DisplayName("POST /redo sollte DELETE wiederherstellen und 204 zurückgeben")
        void redoLastAction_shouldRedoDeleteAndReturnNoContent() throws Exception {
            // Given: Todo erstellen und dann löschen
            Todo todoToDelete = todoRepository.save(new Todo(null, Status.IN_PROGRESS, "To be deleted then restored"));
            String todoId = todoToDelete.id();

            mockMvc.perform(delete("/api/todo/" + todoId))
                    .andExpect(status().isNoContent());

            Assertions.assertThat(todoRepository.existsById(todoId)).isFalse();
            assertThat(historyEntryRepository.count()).isEqualTo(1); // Nur Delete wird historisiert

            mockMvc.perform(post("/api/todo/undo"))
                    .andExpect(status().isNoContent());
            // When: Undo aufrufen
            mockMvc.perform(post("/api/todo/redo"))
                    .andExpect(status().isNoContent());

            // Then: Todo sollte entfernt sein, History-Eintrag nicht als undone markiert
            Optional<Todo> deletedTodoOpt = todoRepository.findById(todoId);
            Assertions.assertThat(deletedTodoOpt).isEmpty();

            Optional<HistoryEntry<?>> historyEntryOpt = historyEntryRepository.findFirstByEntityIdOrderByDoneAtDesc(todoId);
            Assertions.assertThat(historyEntryOpt).isPresent();
            Assertions.assertThat(historyEntryOpt.get().undoneAt()).isNull();
        }

        @Test
        @DisplayName("POST /redo sollte 204 zurückgeben, wenn nichts zum Wiederherstellen da ist")
        void redoLastAction_shouldReturnNoContent_whenNothingToUndo() throws Exception {
            // Given: Leere Repositories (durch setUp)
            assertThat(todoRepository.count()).isZero();
            Assertions.assertThat(historyEntryRepository.count()).isZero();

            // When: Undo aufrufen
            mockMvc.perform(post("/api/todo/redo"))
                    .andExpect(status().isNoContent());

            // Then: Repositories sollten immer noch leer sein
            assertThat(todoRepository.count()).isZero();
            assertThat(historyEntryRepository.count()).isZero();
        }
    }

}