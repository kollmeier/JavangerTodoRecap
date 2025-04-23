package ckollmeier.de.javangertodorecap.service;

import ckollmeier.de.javangertodorecap.converter.TodoConverter;
import ckollmeier.de.javangertodorecap.converter.TodoDTOConverter;
import ckollmeier.de.javangertodorecap.dto.TodoDTO;
import ckollmeier.de.javangertodorecap.dto.TodoInputDTO;
import ckollmeier.de.javangertodorecap.entity.Todo;
import ckollmeier.de.javangertodorecap.exception.NotFoundException;
import ckollmeier.de.javangertodorecap.generator.IDGenerator;
import ckollmeier.de.javangertodorecap.repository.TodoRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service zur Verwaltung von Todos.
 */
@Service
@RequiredArgsConstructor
public class TodoService {
    /**
     * Repository für die Verwaltung von Todos.
     */
    private final TodoRepository todoRepository;

    /**
     * Holt alle @{link Todo}s aus dem Repository und gibt sie als Liste von @{link TodoDTO}s zurück.
     * @return Liste von Todos als @{link TodoDTO}
     */
    public List<TodoDTO> getAllTodos() {
        return TodoDTOConverter.convert(todoRepository.findAll());
    }

    /**
     * Fügt ein aus dem übergebenen DTO erstelltes {@link Todo} mit generierter Id in das Repository ein.
     * @param todoInputDTO zu erstellendes Todo
     * @return erstelltes Todo als @{link TodoDTO}
     */
    public TodoDTO addTodo(final @NonNull TodoInputDTO todoInputDTO) {
        Todo todo = TodoConverter.convert(todoInputDTO).withId(IDGenerator.generateID());
        todoRepository.save(todo);
        return TodoDTOConverter.convert(todo);
    }

    /**
     * Ändert ein Todo anhand seiner Id im Repository.
     * @param id Id des zu ändernden Todos
     * @param todoInputDTO zu ändernde Daten
     * @return geändertes Todo als @{link TodoDTO}
     */
    public TodoDTO updateTodo(final @NonNull String id, final @NonNull TodoInputDTO todoInputDTO) {
        if (!todoRepository.existsById(id)) {
            throw new NotFoundException("Todo not found");
        }

        Todo todo = TodoConverter.convert(todoInputDTO).withId(id);
        todoRepository.save(todo);
        return TodoDTOConverter.convert(todo);
    }

    /**
     * Holt ein Todo anhand seiner Id aus dem Repository und gibt es als @{link TodoDTO} zurück.
     * @param id Id des zu suchenden Todos
     * @return Todo als @{link TodoDTO}
     */
    public TodoDTO getTodoById(final @NonNull String id) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new NotFoundException("Todo not found"));
        return TodoDTOConverter.convert(todo);
    }

    /**
     * Löscht ein Todo anhand seiner Id aus dem Repository.
     * @param id Id des zu löschenden Todos
     */
    public void deleteTodo(final @NonNull String id) {
        if (!todoRepository.existsById(id)) {
            throw new NotFoundException("Todo not found");
        }
        todoRepository.deleteById(id);
    }
}
