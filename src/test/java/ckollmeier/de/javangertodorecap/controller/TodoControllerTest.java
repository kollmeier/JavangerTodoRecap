package ckollmeier.de.javangertodorecap.controller;

import ckollmeier.de.javangertodorecap.dto.TodoInputDTO;
import ckollmeier.de.javangertodorecap.entity.Todo;
import ckollmeier.de.javangertodorecap.enums.Status;
import ckollmeier.de.javangertodorecap.repository.TodoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.bson.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;

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
                .andExpect(status().isOk())
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
}