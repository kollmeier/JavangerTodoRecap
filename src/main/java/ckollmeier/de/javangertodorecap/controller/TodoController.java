package ckollmeier.de.javangertodorecap.controller;

import ckollmeier.de.javangertodorecap.dto.TodoDTO;
import ckollmeier.de.javangertodorecap.dto.TodoInputDTO;
import ckollmeier.de.javangertodorecap.service.HistoryService;
import ckollmeier.de.javangertodorecap.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * Service zur Verwaltung der Historie.
     */
    private final HistoryService historyService;

    /**
     * Holt alle Todos aus dem Service und gibt sie als Liste von @{link TodoDTO}s zurück.
     * @return Liste von Todos als @{link TodoDTO}
     */
    @GetMapping
    public List<TodoDTO> getAllTodos() {
        return todoService.getAllTodos();
    }

    /**
     * Holt ein Todo anhand seiner Id aus dem Service und gibt es als @{link TodoDTO} zurück.
     * @param id Id des zu suchenden Todos
     * @return Todo als @{link TodoDTO}
     */
    @GetMapping("/{id}")
    public TodoDTO getTodoById(final @PathVariable String id) {
        return todoService.getTodoById(id);
    }

    /**
     * Fügt ein aus dem übergebenen DTO erstelltes {@link ckollmeier.de.javangertodorecap.entity.Todo} mit generierter Id in das Repository ein.
     * @param todoInputDTO zu erstellendes Todo
     * @return erstelltes Todo als @{link TodoDTO}
     */
    @PostMapping
    public ResponseEntity<TodoDTO> addTodo(final @RequestBody TodoInputDTO todoInputDTO, final @RequestParam(required = false, defaultValue = "false") Boolean force) {
        return new ResponseEntity<>(todoService.addTodo(todoInputDTO, force), HttpStatus.CREATED);
    }

    /**
     * Ändert ein Todo anhand seiner Id im Repository.
     * @param id Id des zu ändernden Todos
     * @param todoInputDTO zu ändernde Daten
     * @return geändertes Todo als @{link TodoDTO}
     */
    @PutMapping("/{id}")
    public TodoDTO updateTodo(final @PathVariable String id, final @RequestBody TodoInputDTO todoInputDTO) {
        return todoService.updateTodo(id, todoInputDTO);
    }

    /**
     * Löscht ein Todo anhand seiner Id aus dem Repository.
     * @param id Id des zu löschenden Todos
     * @return Statuscode 204
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteTodo(final @PathVariable String id) {
        todoService.deleteTodo(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Macht die letzte Aktion rückgängig.
     * @return Statuscode 200
     */
    @PostMapping("/undo")
    public List<TodoDTO> undoLastEntry() {
        historyService.undoLastEntry();
        return todoService.getAllTodos();
    }

    /**
     * Stellt die letzte rückgängig gemachte Aktion wieder her.
     * @return Statuscode 200
     */
    @PostMapping("/redo")
    public List<TodoDTO> redoLastEntry() {
        historyService.redoLastEntry();
        return todoService.getAllTodos();
    }
}
