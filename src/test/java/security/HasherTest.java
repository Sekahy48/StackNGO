package security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HasherTest {

    private Salt salt;
    private String password1 = "prueba123";

    @BeforeEach
    public void setUp() {
        this.salt = Salt.generate();
    }

    @Test
    public void testHasher() {

        String password2 = "prueba234";

        byte[] hash1 = Hasher.hash(this.password1, salt);
        byte[] hash2 = Hasher.hash(password2, salt);

        assertEquals(hash1, hash1);
        assertEquals(hash2, hash2);
        assertNotEquals(hash1, hash2);
    }

    @Test
    public void testWrongPasswordVerify() {

        String password2 = "prueba234";

        byte[] hash = Hasher.hash(this.password1, salt);

        assertFalse(Hasher.verify(password2, this.salt, hash));
    }

    @Test
    public void testWrongSaltVerify() {

        Salt newSalt = Salt.generate();

        byte[] hash = Hasher.hash(password1, this.salt);

        assertFalse(Hasher.verify(password1, newSalt, hash));
    }

    @Test
    public void testAllCorrectVerify() {

        byte[] hash = Hasher.hash(this.password1, this.salt);

        assertTrue(Hasher.verify(this.password1, this.salt, hash));
    }

    @Test
    public void testNullSalt() {

        Salt testSalt = null;

        assertThrows(RuntimeException.class, () -> {
            Hasher.hash(this.password1, testSalt);
        });
    }

    @Test
    public void testNullPassword() {

        String testPassword = null;

        assertThrows(RuntimeException.class, () -> {
            Hasher.hash(testPassword, this.salt);
        });
    }
}