package ckollmeier.de.javangertodorecap.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Represents a request to the ChatGPT Completion API.
 * This class encapsulates the data needed to make a request to the API.
 */
@Getter
@RequiredArgsConstructor
public final class ChatGPTCompletionAPIRequest {
    /**
     * The model to use for the completion.
     */
    private final String model;
    /**
     * The list of messages to use as context for the completion.
     */
    private final List<ChatGPTMessage> messages;
    /**
     * The format of the response.
     * This is used to specify that the response should be in JSON format.
     */
    private final Object response_format;
}
