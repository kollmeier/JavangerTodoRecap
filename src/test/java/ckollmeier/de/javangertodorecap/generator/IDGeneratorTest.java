package ckollmeier.de.javangertodorecap.generator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IDGeneratorTest {

    @Test
    void generateID_shouldGenerateUniqueID() {
        // When
        String id = IDGenerator.generateID();

        // Then
        assertNotNull(id);
        assertEquals(36, id.length());
    }
}