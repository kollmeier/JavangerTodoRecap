package ckollmeier.de.javangertodorecap.service;

import ckollmeier.de.javangertodorecap.converter.TodoConverter;
import ckollmeier.de.javangertodorecap.converter.TodoDTOConverter;
import ckollmeier.de.javangertodorecap.dto.TodoDTO;
import ckollmeier.de.javangertodorecap.dto.TodoInputDTO;
import ckollmeier.de.javangertodorecap.entity.Todo;
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
}
