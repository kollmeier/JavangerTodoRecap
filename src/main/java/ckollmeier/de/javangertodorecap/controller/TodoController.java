package ckollmeier.de.javangertodorecap.controller;

import ckollmeier.de.javangertodorecap.dto.TodoDTO;
import ckollmeier.de.javangertodorecap.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
}
