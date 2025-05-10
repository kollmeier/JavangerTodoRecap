package ckollmeier.de.javangertodorecap.service;

import ckollmeier.de.javangertodorecap.converter.TodoConverter;
import ckollmeier.de.javangertodorecap.converter.TodoDTOConverter;
import ckollmeier.de.javangertodorecap.dto.OrthopgraphyCheckDTO;
import ckollmeier.de.javangertodorecap.dto.TodoDTO;
import ckollmeier.de.javangertodorecap.dto.TodoInputDTO;
import ckollmeier.de.javangertodorecap.entity.Todo;
import ckollmeier.de.javangertodorecap.enums.Action;
import ckollmeier.de.javangertodorecap.enums.Entity;
import ckollmeier.de.javangertodorecap.exception.NotFoundException;
import ckollmeier.de.javangertodorecap.exception.ChatGPTOpenAIResultException;
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
     * Service zur Verwaltung von History.
     */
    private final HistoryService historyService;

    /**
     * Service to talk to ChatGPT.
     */
    private final ChatGPTService chatGPTService;

    /**
     * Holt alle @{link Todo}s aus dem Repository und gibt sie als Liste von @{link TodoDTO}s zurück.
     * @return Liste von Todos als @{link TodoDTO}
     */
    public List<TodoDTO> getAllTodos() {
        return TodoDTOConverter.convert(todoRepository.findAllByOrderByCreatedAtDesc());
    }

    /**
     * Fügt ein aus dem übergebenen DTO erstelltes {@link Todo} mit generierter Id in das Repository ein.
     * @param todoInputDTO zu erstellendes Todo
     * @return erstelltes Todo als @{link TodoDTO}
     */
    public TodoDTO addTodo(final @NonNull TodoInputDTO todoInputDTO) {
        Todo todo = TodoConverter.convert(todoInputDTO).withId(IDGenerator.generateID());
        try {
            OrthopgraphyCheckDTO orthopgraphyCheckDTO = chatGPTService.getOrthographyCheck(todo.description());
            if (orthopgraphyCheckDTO != null && orthopgraphyCheckDTO.errorCount() > 0) {
                todo = todo.withDescription(orthopgraphyCheckDTO.errors().getLast().fullCorrectedText());
            }
        } catch (ChatGPTOpenAIResultException e) {
            // ignore
        } // Let ChatGPTOpenAIRequestException pass through
        todoRepository.save(todo);
        historyService.addEntry(Action.CREATE, Entity.TODO, todo.id(), todo, null);
        return TodoDTOConverter.convert(todo);
    }

    /**
     * Ändert ein Todo anhand seiner Id im Repository.
     * @param id Id des zu ändernden Todos
     * @param todoInputDTO zu ändernde Daten
     * @return geändertes Todo als @{link TodoDTO}
     */
    public TodoDTO updateTodo(final @NonNull String id, final @NonNull TodoInputDTO todoInputDTO) {
        Todo previousTodo = todoRepository.findById(id).orElseThrow(() -> new NotFoundException("Todo not found"));

        Todo todo = TodoConverter.convert(todoInputDTO).withId(id);
        todoRepository.save(todo);
        historyService.addEntry(Action.UPDATE, Entity.TODO, todo.id(), todo, previousTodo);
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
        Todo previousTodo = todoRepository.findById(id).orElseThrow(() -> new NotFoundException("Todo not found"));
        historyService.addEntry(Action.DELETE, Entity.TODO, previousTodo.id(), null, previousTodo);
        todoRepository.deleteById(id);
    }
}
