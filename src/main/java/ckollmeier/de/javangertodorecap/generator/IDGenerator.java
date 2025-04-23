package ckollmeier.de.javangertodorecap.generator;

/**
 * Util-Klasse zum Generieren von IDs.
 */
public final class IDGenerator {
    private IDGenerator() {
        // Util-Klasse
        throw new IllegalStateException("Utility class");
    }

    /**
     * Generiert eine eindeutige ID.
     * @return eindeutige ID
     */
    public static String generateID() {
        return java.util.UUID.randomUUID().toString();
    }
}
