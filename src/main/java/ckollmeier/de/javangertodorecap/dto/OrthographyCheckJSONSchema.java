package ckollmeier.de.javangertodorecap.dto;

/**
 * This class represents the JSON schema for an orthography check.
 */
public class OrthographyCheckJSONSchema {
    /**
     * The type of the schema, which is "json_schema".
     */
    public final String type = "json_schema";

    /**
     * The JSON schema for the orthography check.
     */
    @SuppressWarnings({"checkstyle:VisibilityModifier", "checkstyle:MemberName"})
    public final JsonSchema json_schema = new JsonSchema();

    public class JsonSchema {
        /**
         * The name of the schema, which is "orthography_check".
         */
        public final String name = "orthography_check";
        /**
         * The actual JSON schema as a string.
         */
        public final Object schema = new Schema();
    }

    class Schema {
        public String type = "object";
        public Properties properties = new Properties();
    }

    class Properties {
        public ErrorCount errorCount = new ErrorCount();
        public Errors errors = new Errors();
    }

    class ErrorCount {
        public String type = "integer";
    }

    class Errors {
        public String type = "array";
        public Items items = new Items();
    }

    class Items {
        public String type = "object";
        public ItemProperties properties = new ItemProperties();
    }

    class ItemProperties {
        public TextIndex textIndex = new TextIndex();
        public OriginalText originalText = new OriginalText();
        public CorrectedText correctedText = new CorrectedText();
    }

    class TextIndex {
        public String type = "integer";
    }

    class OriginalText {
        public String type = "string";
    }

    class CorrectedText {
        public String type  = "string";

    }
}
