package ckollmeier.de.javangertodorecap.exception;

public class ChatGPTOpenAIRequestException extends RuntimeException {
    public ChatGPTOpenAIRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatGPTOpenAIRequestException(String message) {
        super(message);
    }
}
