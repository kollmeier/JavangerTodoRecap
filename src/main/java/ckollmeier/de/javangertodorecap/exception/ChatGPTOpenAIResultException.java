package ckollmeier.de.javangertodorecap.exception;

public class ChatGPTOpenAIResultException extends Exception {
    public ChatGPTOpenAIResultException(final String message, final Throwable cause) {
        super(message, cause);
    }
    public ChatGPTOpenAIResultException(final String message) {
        super(message);
    }
}
