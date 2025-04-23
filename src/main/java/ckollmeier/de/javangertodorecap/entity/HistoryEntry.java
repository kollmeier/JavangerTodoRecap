package ckollmeier.de.javangertodorecap.entity;

import ckollmeier.de.javangertodorecap.enums.Action;
import ckollmeier.de.javangertodorecap.enums.Entity;
import lombok.With;

import java.time.Instant;

public record HistoryEntry<T>(
        String id,
        String entityId,
        Action action,
        Entity entity,
        T payload,
        T previousPayload,
        Instant doneAt,
        @With
        Instant undoneAt
) {
}
