package ckollmeier.de.javangertodorecap.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to load properties from application.properties with the prefix "config".
 * It provides access to the configured properties.
 */
@ConfigurationProperties(prefix = "openai.api")
@Configuration
@Getter
@Setter
public class OpenAIConfig {
    /** The API key loaded from the configuration. */
    private String key;
}
