package ckollmeier.de.javangertodorecap.controller;

import ckollmeier.de.javangertodorecap.entity.Todo;
import ckollmeier.de.javangertodorecap.enums.Status;
import ckollmeier.de.javangertodorecap.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}