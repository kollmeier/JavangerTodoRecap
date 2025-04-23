package ckollmeier.de.javangertodorecap.service;

import ckollmeier.de.javangertodorecap.entity.HistoryEntry;
import ckollmeier.de.javangertodorecap.entity.Todo;
import ckollmeier.de.javangertodorecap.enums.Action;
import ckollmeier.de.javangertodorecap.enums.Entity;
// IDGenerator wird nicht direkt gemockt, da es statisch ist, aber wir überprüfen das Ergebnis
// import ckollmeier.de.javangertodorecap.generator.IDGenerator;
import ckollmeier.de.javangertodorecap.enums.Status; // Import hinzugefügt
import ckollmeier.de.javangertodorecap.repository.HistoryEntryRepository;
import ckollmeier.de.javangertodorecap.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit-Tests für den {@link HistoryService} unter Verwendung des Given-When-Then-Musters.
 */
@ExtendWith(MockitoExtension.class) // Mockito initialisieren
class HistoryServiceTest {

    // Mocks für alle Abhängigkeiten
    @Mock
    private HistoryEntryRepository historyEntryRepository;
    @Mock
    private TodoRepository todoRepository;

    // Injiziert die Mocks in die zu testende Service-Instanz
    @InjectMocks
    private HistoryService historyService;

    // ArgumentCaptor zum Erfassen von Argumenten, die an Mocks übergeben werden
    @Captor
    private ArgumentCaptor<HistoryEntry> historyEntryCaptor;
    @Captor
    private ArgumentCaptor<Todo> todoCaptor;
    @Captor
    private ArgumentCaptor<String> stringCaptor;

    // Testdaten
    private Todo testTodo;
    private Todo previousTestTodo;
    private String testTodoId;
    private String testHistoryEntryId;
    private Instant fixedTime = Instant.parse("2023-10-27T10:00:00Z"); // Feste Zeit für Tests
    private Instant undoneTime = Instant.parse("2023-10-27T10:05:00Z"); // Feste Zeit für Undone

    @BeforeEach
    void setUp() {
        testTodoId = UUID.randomUUID().toString();
        testHistoryEntryId = UUID.randomUUID().toString();

        // Status hinzugefügt, basierend auf dem Code-Snippet
        testTodo = new Todo(testTodoId, Status.IN_PROGRESS, "Aktuelle Beschreibung");
        // Zustand vor Update/Delete, oder null bei Create
        previousTestTodo = new Todo(testTodoId, Status.OPEN, "Vorherige Beschreibung");
    }

    // --- Tests für addEntry ---
    @Nested
    @DisplayName("addEntry Tests")
    class AddEntryTests {

        @Test
        @DisplayName("sollte einen HistoryEntry mit korrekten Daten speichern")
        void addEntry_shouldSaveHistoryEntryWithCorrectData() {
            // Given
            Action action = Action.CREATE;
            Entity entity = Entity.TODO;
            Todo payload = testTodo;
            Todo previousPayload = null; // Kein vorheriger Zustand bei CREATE

            // When
            historyService.addEntry(action, entity, testTodoId, payload, previousPayload);

            // Then
            // überprüfen, ob historyEntryRepository.save genau einmal aufgerufen wurde
            verify(historyEntryRepository, times(1)).save(historyEntryCaptor.capture());
            // überprüfen, dass offene Redo-Schritte gelöscht werden
            verify(historyEntryRepository, times(1)).deleteAllByUndoneAtIsNotNull();

            // Überprüfen der erfassten HistoryEntry-Daten
            HistoryEntry<?> capturedEntry = historyEntryCaptor.getValue();
            assertThat(capturedEntry).isNotNull();
            assertThat(capturedEntry.id()).isNotNull().isNotEmpty(); // ID wird generiert (nicht null/leer)
            assertThat(capturedEntry.entityId()).isEqualTo(testTodoId);
            assertThat(capturedEntry.action()).isEqualTo(action);
            assertThat(capturedEntry.entity()).isEqualTo(entity);
            assertThat(capturedEntry.payload()).isEqualTo(payload);
            assertThat(capturedEntry.previousPayload()).isEqualTo(previousPayload);
            assertThat(capturedEntry.doneAt()).isNotNull().isBeforeOrEqualTo(Instant.now()); // Zeitstempel prüfen
            assertThat(capturedEntry.undoneAt()).isNull(); // undoneAt sollte null sein

            // Sicherstellen, dass keine Interaktionen mit todoRepository stattfanden
            verifyNoInteractions(todoRepository);
        }
    }

    // --- Tests für undoLastEntry ---
    @Nested
    @DisplayName("undoLastEntry Tests")
    class UndoLastEntryTests {

        @Test
        @DisplayName("sollte nichts tun, wenn kein letzter Eintrag vorhanden ist")
        void undoLastEntry_shouldDoNothing_whenNoEntryExists() {
            // Given
            // Mocken, dass kein Eintrag gefunden wird
            when(historyEntryRepository.findFirstByUndoneAtIsNullOrderByDoneAtDesc()).thenReturn(Optional.empty());

            // When
            historyService.undoLastEntry();

            // Then
            // Überprüfen, ob die Suchmethode aufgerufen wurde
            verify(historyEntryRepository, times(1)).findFirstByUndoneAtIsNullOrderByDoneAtDesc();
            // Sicherstellen, dass keine weiteren Interaktionen stattfanden
            verifyNoMoreInteractions(historyEntryRepository);
            verifyNoInteractions(todoRepository);
        }

        @Test
        @DisplayName("sollte Todo löschen, wenn die letzte Aktion CREATE war")
        void undoLastEntry_shouldDeleteTodo_whenLastActionWasCreate() {
            // Given
            HistoryEntry<Todo> createEntry = new HistoryEntry<>(
                    testHistoryEntryId, testTodoId, Action.CREATE, Entity.TODO, testTodo, null, fixedTime, null
            );
            when(historyEntryRepository.findFirstByUndoneAtIsNullOrderByDoneAtDesc()).thenReturn(Optional.of(createEntry));

            // When
            historyService.undoLastEntry();

            // Then
            // Überprüfen der Repository-Interaktionen
            verify(historyEntryRepository, times(1)).findFirstByUndoneAtIsNullOrderByDoneAtDesc();
            verify(todoRepository, times(1)).deleteById(stringCaptor.capture());
            verify(historyEntryRepository, times(1)).save(historyEntryCaptor.capture()); // Zum Setzen von undoneAt

            // Überprüfen der Argumente
            assertThat(stringCaptor.getValue()).isEqualTo(testTodoId); // Korrekte ID zum Löschen
            HistoryEntry<?> savedEntry = historyEntryCaptor.getValue();
            assertThat(savedEntry.id()).isEqualTo(testHistoryEntryId);
            assertThat(savedEntry.undoneAt()).isNotNull(); // undoneAt muss gesetzt sein

            // Sicherstellen, dass save nicht aufgerufen wurde
            verify(todoRepository, never()).save(any(Todo.class));
        }

        @Test
        @DisplayName("sollte vorherigen Todo-Zustand speichern, wenn die letzte Aktion UPDATE war")
        void undoLastEntry_shouldSavePreviousTodo_whenLastActionWasUpdate() {
            // Given
            HistoryEntry<Todo> updateEntry = new HistoryEntry<>(
                    testHistoryEntryId, testTodoId, Action.UPDATE, Entity.TODO, testTodo, previousTestTodo, fixedTime, null
            );
            when(historyEntryRepository.findFirstByUndoneAtIsNullOrderByDoneAtDesc()).thenReturn(Optional.of(updateEntry));

            // When
            historyService.undoLastEntry();

            // Then
            // Überprüfen der Repository-Interaktionen
            verify(historyEntryRepository, times(1)).findFirstByUndoneAtIsNullOrderByDoneAtDesc();
            verify(todoRepository, times(1)).save(todoCaptor.capture()); // Vorherigen Zustand speichern
            verify(historyEntryRepository, times(1)).save(historyEntryCaptor.capture()); // Zum Setzen von undoneAt

            // Überprüfen der Argumente
            assertThat(todoCaptor.getValue()).isEqualTo(previousTestTodo); // Vorheriger Zustand wird gespeichert
            HistoryEntry<?> savedEntry = historyEntryCaptor.getValue();
            assertThat(savedEntry.id()).isEqualTo(testHistoryEntryId);
            assertThat(savedEntry.undoneAt()).isNotNull();

            // Sicherstellen, dass deleteById nicht aufgerufen wurde
            verify(todoRepository, never()).deleteById(anyString());
        }

        @Test
        @DisplayName("sollte vorherigen Todo-Zustand speichern, wenn die letzte Aktion DELETE war")
        void undoLastEntry_shouldSavePreviousTodo_whenLastActionWasDelete() {
            // Given
            // Bei DELETE ist payload oft null, previousPayload enthält das gelöschte Objekt
            HistoryEntry<Todo> deleteEntry = new HistoryEntry<>(
                    testHistoryEntryId, testTodoId, Action.DELETE, Entity.TODO, null, previousTestTodo, fixedTime, null
            );
            when(historyEntryRepository.findFirstByUndoneAtIsNullOrderByDoneAtDesc()).thenReturn(Optional.of(deleteEntry));

            // When
            historyService.undoLastEntry();

            // Then
            // Überprüfen der Repository-Interaktionen
            verify(historyEntryRepository, times(1)).findFirstByUndoneAtIsNullOrderByDoneAtDesc();
            verify(todoRepository, times(1)).save(todoCaptor.capture()); // Gelöschtes Objekt wieder speichern
            verify(historyEntryRepository, times(1)).save(historyEntryCaptor.capture()); // Zum Setzen von undoneAt

            // Überprüfen der Argumente
            assertThat(todoCaptor.getValue()).isEqualTo(previousTestTodo); // Gelöschtes Objekt wird wieder gespeichert
            HistoryEntry<?> savedEntry = historyEntryCaptor.getValue();
            assertThat(savedEntry.id()).isEqualTo(testHistoryEntryId);
            assertThat(savedEntry.undoneAt()).isNotNull();

            // Sicherstellen, dass deleteById nicht aufgerufen wurde
            verify(todoRepository, never()).deleteById(anyString());
        }
    }

    // --- Tests für redoLastEntry ---
    @Nested
    @DisplayName("redoLastEntry Tests")
    class RedoLastEntryTests {

        @Test
        @DisplayName("sollte nichts tun, wenn kein letzter rückgängig gemachter Eintrag vorhanden ist")
        void redoLastEntry_shouldDoNothing_whenNoUndoneEntryExists() {
            // Given
            // Mocken, dass kein rückgängig gemachter Eintrag gefunden wird
            when(historyEntryRepository.findFirstByUndoneAtIsNotNullOrderByDoneAtAsc()).thenReturn(Optional.empty());

            // When
            historyService.redoLastEntry();

            // Then
            // Überprüfen, ob die Suchmethode aufgerufen wurde
            verify(historyEntryRepository, times(1)).findFirstByUndoneAtIsNotNullOrderByDoneAtAsc();
            // Sicherstellen, dass keine weiteren Interaktionen stattfanden
            verifyNoMoreInteractions(historyEntryRepository);
            verifyNoInteractions(todoRepository);
        }

        @Test
        @DisplayName("sollte Todo speichern (payload), wenn die rückgängig gemachte Aktion CREATE war")
        void redoLastEntry_shouldSaveTodoPayload_whenUndoneActionWasCreate() {
            // Given
            // Ein Eintrag, der durch undo (eines CREATE) rückgängig gemacht wurde
            HistoryEntry<Todo> undoneCreateEntry = new HistoryEntry<>(
                    testHistoryEntryId, testTodoId, Action.CREATE, Entity.TODO, testTodo, null, fixedTime, undoneTime // undoneAt ist gesetzt
            );
            when(historyEntryRepository.findFirstByUndoneAtIsNotNullOrderByDoneAtAsc()).thenReturn(Optional.of(undoneCreateEntry));

            // When
            historyService.redoLastEntry();

            // Then
            // Überprüfen der Repository-Interaktionen
            verify(historyEntryRepository, times(1)).findFirstByUndoneAtIsNotNullOrderByDoneAtAsc();
            verify(todoRepository, times(1)).save(todoCaptor.capture()); // Payload (das erstellte Todo) speichern
            verify(historyEntryRepository, times(1)).save(historyEntryCaptor.capture()); // Zum Zurücksetzen von undoneAt

            // Überprüfen der Argumente
            assertThat(todoCaptor.getValue()).isEqualTo(testTodo); // Payload wird gespeichert
            HistoryEntry<?> savedEntry = historyEntryCaptor.getValue();
            assertThat(savedEntry.id()).isEqualTo(testHistoryEntryId);
            assertThat(savedEntry.undoneAt()).isNull(); // undoneAt muss wieder null sein

            // Sicherstellen, dass deleteById nicht aufgerufen wurde
            verify(todoRepository, never()).deleteById(anyString());
        }

        @Test
        @DisplayName("sollte Todo speichern (payload), wenn die rückgängig gemachte Aktion UPDATE war")
        void redoLastEntry_shouldSaveTodoPayload_whenUndoneActionWasUpdate() {
            // Given
            // Ein Eintrag, der durch undo (eines UPDATE) rückgängig gemacht wurde
            HistoryEntry<Todo> undoneUpdateEntry = new HistoryEntry<>(
                    testHistoryEntryId, testTodoId, Action.UPDATE, Entity.TODO, testTodo, previousTestTodo, fixedTime, undoneTime // undoneAt ist gesetzt
            );
            when(historyEntryRepository.findFirstByUndoneAtIsNotNullOrderByDoneAtAsc()).thenReturn(Optional.of(undoneUpdateEntry));

            // When
            historyService.redoLastEntry();

            // Then
            // Überprüfen der Repository-Interaktionen
            verify(historyEntryRepository, times(1)).findFirstByUndoneAtIsNotNullOrderByDoneAtAsc();
            verify(todoRepository, times(1)).save(todoCaptor.capture()); // Payload (der neue Zustand nach Update) speichern
            verify(historyEntryRepository, times(1)).save(historyEntryCaptor.capture()); // Zum Zurücksetzen von undoneAt

            // Überprüfen der Argumente
            assertThat(todoCaptor.getValue()).isEqualTo(testTodo); // Payload (neuer Zustand) wird gespeichert
            HistoryEntry<?> savedEntry = historyEntryCaptor.getValue();
            assertThat(savedEntry.id()).isEqualTo(testHistoryEntryId);
            assertThat(savedEntry.undoneAt()).isNull(); // undoneAt muss wieder null sein

            // Sicherstellen, dass deleteById nicht aufgerufen wurde
            verify(todoRepository, never()).deleteById(anyString());
        }

        @Test
        @DisplayName("sollte Todo löschen, wenn die rückgängig gemachte Aktion DELETE war")
        void redoLastEntry_shouldDeleteTodo_whenUndoneActionWasDelete() {
            // Given
            // Ein Eintrag, der durch undo (eines DELETE) rückgängig gemacht wurde
            // previousPayload enthält das Objekt, das wiederhergestellt wurde; payload ist oft null
            HistoryEntry<Todo> undoneDeleteEntry = new HistoryEntry<>(
                    testHistoryEntryId, testTodoId, Action.DELETE, Entity.TODO, null, previousTestTodo, fixedTime, undoneTime // undoneAt ist gesetzt
            );
            when(historyEntryRepository.findFirstByUndoneAtIsNotNullOrderByDoneAtAsc()).thenReturn(Optional.of(undoneDeleteEntry));

            // When
            historyService.redoLastEntry();

            // Then
            // Überprüfen der Repository-Interaktionen
            verify(historyEntryRepository, times(1)).findFirstByUndoneAtIsNotNullOrderByDoneAtAsc();
            verify(todoRepository, times(1)).deleteById(stringCaptor.capture()); // Todo wieder löschen
            verify(historyEntryRepository, times(1)).save(historyEntryCaptor.capture()); // Zum Zurücksetzen von undoneAt

            // Überprüfen der Argumente
            assertThat(stringCaptor.getValue()).isEqualTo(testTodoId); // Korrekte ID zum Löschen
            HistoryEntry<?> savedEntry = historyEntryCaptor.getValue();
            assertThat(savedEntry.id()).isEqualTo(testHistoryEntryId);
            assertThat(savedEntry.undoneAt()).isNull(); // undoneAt muss wieder null sein

            // Sicherstellen, dass save nicht aufgerufen wurde
            verify(todoRepository, never()).save(any(Todo.class));
        }
    }
}