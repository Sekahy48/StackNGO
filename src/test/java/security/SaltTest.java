package security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class SaltTest {

    private Salt salt;

    @BeforeEach
    public void setUp() {

        this.salt = Salt.generate();
    }

    @Test
    public void testGenerate() {
        assertEquals(16, salt.getValue().length);
    }
}