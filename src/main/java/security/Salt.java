package security;

import java.security.SecureRandom;

/**
 * 
 * Class that generates the equivalent of a random seed to hash the password
 * 
 */
public class Salt {

    private final byte[] value;
    private static final int saltLength = 16;
    
    public Salt(byte[] value) {
        this.value = value;
    }

    /**
     * 
     * Method that creates a new {@code Salt} to hash the password
     * 
     * @return new {@code Salt}
     */
    public static Salt generate() {

        byte[] bytes = new byte[saltLength];
        new SecureRandom().nextBytes(bytes);

        return new Salt(bytes);
    }

    /**
     * 
     * Method that returns the bytes used to hash
     * 
     * @return bytes used to hash
     */
    public byte[] getValue() {

        return this.value;
    }
}