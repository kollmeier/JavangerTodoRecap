package ckollmeier.de.javangertodorecap.service;

import ckollmeier.de.javangertodorecap.configuration.OpenAIConfig;
import ckollmeier.de.javangertodorecap.dto.ChatGPTCompletionAPIRequest;
import ckollmeier.de.javangertodorecap.dto.ChatGPTCompletionAPIResponse;
import ckollmeier.de.javangertodorecap.dto.ChatGPTMessage;
import ckollmeier.de.javangertodorecap.dto.OrthopgraphyCheckDTO;
import ckollmeier.de.javangertodorecap.exception.OpenAIResultException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

@Service
public class ChatGPTService {
    /**
     * Rest client.
     */
    private final RestClient restClient;

    /**
     * Instantiates a new Chat GPT service.
     * @param openAIConfig the open ai config
     * @param clientBuilder the client builder
     */
    public ChatGPTService(final OpenAIConfig openAIConfig, final RestClient.Builder clientBuilder) {
        this.restClient = clientBuilder
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader("Authorization", "Bearer " + openAIConfig.getKey())
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    /**
     * Checks orthography via ChatGPT.
     * @param input the input
     * @return the orthography check dto
     */
    public OrthopgraphyCheckDTO getOrthopgraphyCheck(final String input) throws OpenAIResultException {
        ChatGPTCompletionAPIResponse response = Objects.requireNonNull(restClient.post()
                        .uri("")
                        .body(new ChatGPTCompletionAPIRequest(
                                "gpt-4.1",
                                List.of(new ChatGPTMessage(
                                        "user",
                                        String.format("Rechtschreibprüfung: \"%s\"", input))
                                )
                            )
                        ))
                        .retrieve()
                        .body(ChatGPTCompletionAPIResponse.class);
        ObjectMapper objectMapper = new ObjectMapper();
        if (response != null) {
            OrthopgraphyCheckDTO dto = null;
            try {
                dto = objectMapper.readValue(response.choices().getFirst().message().content(), OrthopgraphyCheckDTO.class);
            } catch (JsonProcessingException e) {
                    throw new OpenAIResultException("Unexpected Result from OpenAI", e);
            }
            return dto;
        }
        return null;
    }
}
