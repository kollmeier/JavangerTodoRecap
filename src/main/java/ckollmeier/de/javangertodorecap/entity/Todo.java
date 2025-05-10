package ckollmeier.de.javangertodorecap.entity;

import ckollmeier.de.javangertodorecap.enums.Status;
import lombok.With;

import java.time.Instant;
import java.time.ZonedDateTime;

@With
public record Todo(
    String id,
    Status status,
    String description,
    Instant createdAt
) {
}
