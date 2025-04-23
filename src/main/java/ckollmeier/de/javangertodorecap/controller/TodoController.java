package ckollmeier.de.javangertodorecap.controller;

import ckollmeier.de.javangertodorecap.dto.TodoDTO;
import ckollmeier.de.javangertodorecap.dto.TodoInputDTO;
import ckollmeier.de.javangertodorecap.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller zur Verwaltung von Todos.
 */
@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
public class TodoController {
    /**
     * Service zur Verwaltung von Todos.
     */
    private final TodoService todoService;

    /**
     * Holt alle Todos aus dem Service und gibt sie als Liste von @{link TodoDTO}s zurück.
     * @return Liste von Todos als @{link TodoDTO}
     */
    @GetMapping
    public List<TodoDTO> getAllTodos() {
        return todoService.getAllTodos();
    }

    /**
     * Fügt ein aus dem übergebenen DTO erstelltes {@link ckollmeier.de.javangertodorecap.entity.Todo} mit generierter Id in das Repository ein.
     * @param todoInputDTO zu erstellendes Todo
     * @return erstelltes Todo als @{link TodoDTO}
     */
    @PostMapping
    public TodoDTO addTodo(final @RequestBody TodoInputDTO todoInputDTO) {
        return todoService.addTodo(todoInputDTO);
    }
}
