package ckollmeier.de.javangertodorecap.service;

import ckollmeier.de.javangertodorecap.configuration.OpenAIConfig;
import ckollmeier.de.javangertodorecap.dto.ChatGPTCompletionAPIRequest;
import ckollmeier.de.javangertodorecap.dto.ChatGPTCompletionAPIResponse;
import ckollmeier.de.javangertodorecap.dto.OrthopgraphyCheckDTO;
import ckollmeier.de.javangertodorecap.exception.ChatGPTOpenAIRequestException;
import ckollmeier.de.javangertodorecap.exception.ChatGPTOpenAIResultException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
@AutoConfigureMockRestServiceServer
@ExtendWith(MockitoExtension.class)
class ChatGPTServiceTest {
    private ChatGPTService chatGPTService;

    @Mock
    private OpenAIConfig openAIConfig;

    @Mock
    private RestClient.Builder restClientBuilder;

    @Mock
    private RestClient restClient;

    private ObjectMapper objectMapper = new ObjectMapper();

    RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);


    @BeforeEach
    public void setUp() {
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.RequestBodyUriSpec uriSpec = mock(RestClient.RequestBodyUriSpec.class);
        when(restClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.body(any(ChatGPTCompletionAPIRequest.class))).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        
        when(restClientBuilder.baseUrl(anyString())).thenReturn(restClientBuilder);
        when(restClientBuilder.defaultHeader(anyString(), anyString())).thenReturn(restClientBuilder);
        when(restClientBuilder.build()).thenReturn(restClient);

        chatGPTService = new ChatGPTService(openAIConfig, restClientBuilder);

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    void getOrthopgraphyCheck() throws ChatGPTOpenAIResultException, JsonProcessingException {
        // Given
        String json = """
                {
                      "id": "chatcmpl-BRzjNgUVDAJTZ6CSpbwvQPsLn8pL2",
                      "object": "chat.completion",
                      "created": 1746011849,
                      "model": "gpt-4.1-2025-04-14",
                      "choices": [
                          {
                              "index": 0,
                              "message": {
                                  "role": "assistant",
                                  "content": "{\\"errorCount\\":1,\\"errors\\":[{\\"originalTextPassage\\":\\"Tset Todo\\",\\"correctedTextPassage\\":\\"Test Todo\\",\\"textPassageIndex\\":0,\\"fullText\\":\\"Tset Todo\\",\\"fullCorrectedText\\":\\"Test Todo\\"}]}",
                                  "refusal": null,
                                  "annotations": []
                              },
                              "logprobs": null,
                              "finish_reason": "stop"
                          }
                      ],
                      "usage": {
                          "prompt_tokens": 180,
                          "completion_tokens": 51,
                          "total_tokens": 231,
                          "prompt_tokens_details": {
                              "cached_tokens": 0,
                              "audio_tokens": 0
                          },
                          "completion_tokens_details": {
                              "reasoning_tokens": 0,
                              "audio_tokens": 0,
                              "accepted_prediction_tokens": 0,
                              "rejected_prediction_tokens": 0
                          }
                      },
                      "service_tier": "default",
                      "system_fingerprint": "fp_b38e740b47"
                  }
                """;
        when(responseSpec.body(ChatGPTCompletionAPIResponse.class)).thenReturn(objectMapper.readValue(json, ChatGPTCompletionAPIResponse.class));

        // When
        OrthopgraphyCheckDTO orthopgraphyCheckDTO = chatGPTService.getOrthographyCheck("Tset Todo");

        // Then
        assertThat(orthopgraphyCheckDTO.errorCount()).isEqualTo(1);
        assertThat(orthopgraphyCheckDTO.errors().getFirst().originalTextPassage()).isEqualTo("Tset Todo");
        assertThat(orthopgraphyCheckDTO.errors().getFirst().correctedTextPassage()).isEqualTo("Test Todo");
        assertThat(orthopgraphyCheckDTO.errors().getFirst().textPassageIndex()).isEqualTo(0);
        assertThat(orthopgraphyCheckDTO.errors().getFirst().fullText()).isEqualTo("Tset Todo");
        assertThat(orthopgraphyCheckDTO.errors().getFirst().fullCorrectedText()).isEqualTo("Test Todo");
    }

    @Test
    public void getOrthographyCheck_shouldThrowChatGPTOpenAIRequestException_whenRestServiceThrowsError() throws ChatGPTOpenAIResultException {
        // Given
        when(responseSpec.body(ChatGPTCompletionAPIResponse.class)).thenThrow(new RuntimeException("Test Exception"));

        // When and Then
        assertThrows(ChatGPTOpenAIRequestException.class, () -> chatGPTService.getOrthographyCheck("Tset Todo"));
    }

    @Test
    public void getOrthographyCheck_shouldThrowChatGPTOpenAIResultException_whenRestServiceReturnsNull() throws ChatGPTOpenAIResultException {
        // Given
        when(responseSpec.body(ChatGPTCompletionAPIResponse.class)).thenReturn(null);

        // When and Then
        assertThrows(ChatGPTOpenAIResultException.class, () -> chatGPTService.getOrthographyCheck("Tset Todo"));
    }

    @Test
    public void getOrthographyCheck_shouldThrowChatGPTOpenAIResultException_whenRestServiceReturnsIncorrectJson() throws JsonProcessingException {
        // Given
        String json = """
                {
                      "id": "chatcmpl-BRzjNgUVDAJTZ6CSpbwvQPsLn8pL2",
                      "object": "chat.completion",
                      "created": 1746011849,
                      "model": "gpt-4.1-2025-04-14",
                      "choices": [
                          {
                              "index": 0,
                              "message": {
                                  "role": "assistant",
                                  "content": "{",
                                  "refusal": null,
                                  "annotations": []
                              },
                              "logprobs": null,
                              "finish_reason": "stop"
                          }
                      ],
                      "usage": {
                          "prompt_tokens": 180,
                          "completion_tokens": 51,
                          "total_tokens": 231,
                          "prompt_tokens_details": {
                              "cached_tokens": 0,
                              "audio_tokens": 0
                          },
                          "completion_tokens_details": {
                              "reasoning_tokens": 0,
                              "audio_tokens": 0,
                              "accepted_prediction_tokens": 0,
                              "rejected_prediction_tokens": 0
                          }
                      },
                      "service_tier": "default",
                      "system_fingerprint": "fp_b38e740b47"
                  }
                """;
        when(responseSpec.body(ChatGPTCompletionAPIResponse.class)).thenReturn(objectMapper.readValue(json, ChatGPTCompletionAPIResponse.class));

        // When and Then
        Throwable cause = assertThrows(ChatGPTOpenAIResultException.class, () -> chatGPTService.getOrthographyCheck("Tset Todo"))
                .getCause();
        assertThat(cause).isInstanceOf(JsonProcessingException.class);
    }

  
}