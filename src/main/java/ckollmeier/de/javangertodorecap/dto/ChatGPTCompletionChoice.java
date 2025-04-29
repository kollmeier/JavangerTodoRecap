package ckollmeier.de.javangertodorecap.dto;

public record ChatGPTCompletionChoice(
        int index,
        ChatGPTMessage message
) {
}
