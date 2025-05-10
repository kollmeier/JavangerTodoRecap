package ckollmeier.de.javangertodorecap.service;

import ckollmeier.de.javangertodorecap.configuration.OpenAIConfig;
import ckollmeier.de.javangertodorecap.dto.ChatGPTCompletionAPIRequest;
import ckollmeier.de.javangertodorecap.dto.ChatGPTCompletionAPIResponse;
import ckollmeier.de.javangertodorecap.dto.ChatGPTMessage;
import ckollmeier.de.javangertodorecap.dto.SpellingValidationDTO;
import ckollmeier.de.javangertodorecap.exception.ChatGPTOpenAIResultException;
import ckollmeier.de.javangertodorecap.exception.ChatGPTOpenAIRequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
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

    private final String spellingValidationJSONSchema;

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
                .without(Option.SCHEMA_VERSION_INDICATOR)
                .with(new JacksonModule());
        SchemaGenerator generator = new SchemaGenerator(configBuilder.build());
        this.spellingValidationJSONSchema = String.format("""
                {
                    "type":"json_schema",
                    "json_schema":{
                        "name":"orthography-check-dto",
                        "schema":%s
                    }
                }"
                """, generator.generateSchema(SpellingValidationDTO.class).toString());
    }

    /**
     * Checks orthography via ChatGPT.
     * @param input the input
     * @return the orthography check dto
     */
    public SpellingValidationDTO validateSpelling(final String input) throws ChatGPTOpenAIResultException {
        ObjectMapper objectMapper = new ObjectMapper();
        ChatGPTCompletionAPIResponse response;
        ChatGPTCompletionAPIRequest request;
        try {
            request = new ChatGPTCompletionAPIRequest(
                    "gpt-4.1",
                    List.of(new ChatGPTMessage(
                            "user",
                            String.format("Rechtschreibprüfung und Grammatikprüfung sowie Stilprüfung: \"%s\"", input))
                    ),
                    objectMapper.readValue(spellingValidationJSONSchema, Object.class)
            );
        } catch (JsonProcessingException e) {
            throw new ChatGPTOpenAIRequestException("Unexpected Error while generating Schema", e);
        }
        try {
            response = Objects.requireNonNull(restClient.post()
                            .uri("")
                            .body(request))
                            .retrieve()
                            .body(ChatGPTCompletionAPIResponse.class);
        } catch (Exception e) {
            throw new ChatGPTOpenAIRequestException("Unexpected Error while requesting OpenAI API", e);
        }
        if (response != null) {
            SpellingValidationDTO dto;
            try {
                dto = objectMapper.readValue(response.choices().getFirst().message().content(), SpellingValidationDTO.class);
            } catch (JsonProcessingException e) {
                throw new ChatGPTOpenAIResultException("Unexpected Error while parsing JSON from OpenAI", e);
            }
            return dto;
        }
        throw new ChatGPTOpenAIResultException("Unexpected Error while requesting OpenAI API: no content returned");
    }
}
