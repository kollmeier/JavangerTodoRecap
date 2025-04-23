package ckollmeier.de.javangertodorecap.service;

import ckollmeier.de.javangertodorecap.entity.HistoryEntry;
import ckollmeier.de.javangertodorecap.entity.Todo;
import ckollmeier.de.javangertodorecap.enums.Action;
import ckollmeier.de.javangertodorecap.enums.Entity;
import ckollmeier.de.javangertodorecap.generator.IDGenerator;
import ckollmeier.de.javangertodorecap.repository.HistoryEntryRepository;
import ckollmeier.de.javangertodorecap.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Der HistoryService verwaltet die Historie von Aktionen, die an Entitäten durchgeführt wurden.
 * Er ermöglicht das Hinzufügen von Einträgen zur Historie, die Informationen über die durchgeführte Aktion,
 * die betroffene Entität, die ID der Entität, die Nutzdaten und den Zeitpunkt der Aktion enthalten.
 */
@Service
@RequiredArgsConstructor // Erzeugt einen Konstruktor mit allen finalen Feldern
public class HistoryService {
    /**
     * Der Repository für den Zugriff auf die HistoryEntry-Datenbank.
     */
    private final HistoryEntryRepository historyEntryRepository; // Repository für den Zugriff auf die HistoryEntry-Datenbank
    /**
     * Der Repository für den Zugriff auf die Todo-Datenbank.
     */
    private final TodoRepository todoRepository;

    /**
     * Fügt einen neuen Eintrag zur Historie hinzu.
     *
     * @param action   Die durchgeführte Aktion (z.B. Erstellen, Aktualisieren, Löschen).
     * @param entity   Die betroffene Entität (z.B. Aufgabe, Benutzer).
     * @param entityId Die ID der betroffenen Entität.
     * @param payload  Die Nutzdaten, die mit der Aktion verbunden sind.
     * @param previousPayload  Die Nutzdaten, die durch die Aktion geändert sind.
     * @param <T>      Der Typ der Nutzdaten.
     *                 Dies ermöglicht die Speicherung verschiedener Datentypen in der Historie.
     *                 z.B. Todo, User, etc.
     */
    public <T> void addEntry(final Action action,
                         final Entity entity,
                         final String entityId,
                         final T payload,
                         final T previousPayload) {
        HistoryEntry<T> entry = new HistoryEntry<>(
                IDGenerator.generateID(),
                entityId,
                action,
                entity,
                payload,
                previousPayload,
                Instant.now(),
                null
        );
        historyEntryRepository.save(entry);
    }

    /**
     * Gibt den letzten History-Eintrag zurück, der noch nicht rückgängig gemacht wurde.
     * @return Optional des letzten History-Eintrags.
     */
    private Optional<? extends HistoryEntry<?>> getLastHistoryEntry() {
        return historyEntryRepository.findFirstByUndoneAtIsNullOrderByDoneAtDesc();
    }



    /**
     * Gibt den letzten History-Eintrag zurück, der rückgängig gemacht wurde.
     *
     * @return Optional des letzten rückgängig gemachten History-Eintrags.
     */
    private Optional<? extends HistoryEntry<?>> getLastUndoneHistoryEntry() {
        return historyEntryRepository.findFirstByUndoneAtIsNotNullOrderByDoneAtAsc();
    }

    /**
     * Macht den letzten Eintrag in der Historie rückgängig.
     * Je nach Aktion wird die entsprechende Operation auf der Todo-Entität ausgeführt.
     */
    public void undoLastEntry() {
        Optional<? extends HistoryEntry<?>> lastHistoryEntry = getLastHistoryEntry();
        if (lastHistoryEntry.isPresent()) {
            HistoryEntry<?> entry = lastHistoryEntry.get();
            switch (entry.action()) {
                case Action.CREATE:
                    if (Objects.requireNonNull(entry.entity()) == Entity.TODO) {
                        todoRepository.deleteById(entry.entityId());
                    }
                    break;
                case Action.DELETE, Action.UPDATE:
                    if (Objects.requireNonNull(entry.entity()) == Entity.TODO) {
                        todoRepository.save((Todo) entry.previousPayload());
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + entry.action());
            }
            historyEntryRepository.save(entry.withUndoneAt(Instant.now()));
        }
    }

    /**
     * Macht den letzten rückgängig gemachten Eintrag wieder rückgängig (redo).
     */
    public void redoLastEntry() {
        Optional<? extends HistoryEntry<?>> lastHistoryEntry = getLastUndoneHistoryEntry();
        if (lastHistoryEntry.isPresent()) {
            HistoryEntry<?> entry = lastHistoryEntry.get();
            switch (entry.action()) {
                case Action.CREATE, Action.UPDATE:
                    if (Objects.requireNonNull(entry.entity()) == Entity.TODO) {
                        todoRepository.save((Todo) entry.payload());
                    }
                    break;
                case Action.DELETE:
                    if (Objects.requireNonNull(entry.entity()) == Entity.TODO) {
                        todoRepository.deleteById(entry.entityId());
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + entry.action());
            }
            historyEntryRepository.save(entry.withUndoneAt(null));
        }
    }
}
