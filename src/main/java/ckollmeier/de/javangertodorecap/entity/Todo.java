package ckollmeier.de.javangertodorecap.entity;

import ckollmeier.de.javangertodorecap.enums.Status;
import lombok.With;

@With
public record Todo(
    String id,
    Status status,
    String description
) {
}
