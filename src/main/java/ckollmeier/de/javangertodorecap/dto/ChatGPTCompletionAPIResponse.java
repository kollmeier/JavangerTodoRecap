package ckollmeier.de.javangertodorecap.dto;

import java.util.List;

public record ChatGPTCompletionAPIResponse(
        List<ChatGPTCompletionChoice> choices
) {
}
