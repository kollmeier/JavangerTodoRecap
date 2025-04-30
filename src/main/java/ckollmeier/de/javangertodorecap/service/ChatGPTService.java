package ckollmeier.de.javangertodorecap.service;

import ckollmeier.de.javangertodorecap.configuration.OpenAIConfig;
import ckollmeier.de.javangertodorecap.dto.ChatGPTCompletionAPIRequest;
import ckollmeier.de.javangertodorecap.dto.ChatGPTCompletionAPIResponse;
import ckollmeier.de.javangertodorecap.dto.ChatGPTMessage;
import ckollmeier.de.javangertodorecap.dto.OrthopgraphyCheckDTO;
import ckollmeier.de.javangertodorecap.exception.OpenAIResultException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victools.jsonschema.generator.*;
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

    private final String orthographyCheckJSONSchema;

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
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON)
                .without(Option.SCHEMA_VERSION_INDICATOR);
        SchemaGenerator generator = new SchemaGenerator(configBuilder.build());
        this.orthographyCheckJSONSchema = String.format("""
                {
                    "type":"json_schema",
                    "json_schema":{
                        "name":"orthography-check-dto",
                        "schema":%s
                    }
                }"
                """, generator.generateSchema(OrthopgraphyCheckDTO.class).toString());
    }

    /**
     * Checks orthography via ChatGPT.
     * @param input the input
     * @return the orthography check dto
     */
    public OrthopgraphyCheckDTO getOrthopgraphyCheck(final String input) throws OpenAIResultException {
        ObjectMapper objectMapper = new ObjectMapper();
        ChatGPTCompletionAPIResponse response;
        try {
            response = Objects.requireNonNull(restClient.post()
                            .uri("")
                            .body(new ChatGPTCompletionAPIRequest(
                                    "gpt-4.1",
                                    List.of(new ChatGPTMessage(
                                            "user",
                                            String.format("Rechtschreibprüfung: \"%s\"", input))
                                    ),
                                    objectMapper.readValue(orthographyCheckJSONSchema, Object.class)
                                )
                            ))
                            .retrieve()
                            .body(ChatGPTCompletionAPIResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (response != null) {
            OrthopgraphyCheckDTO dto;
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
