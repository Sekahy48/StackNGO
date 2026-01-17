package security;

import java.util.HashMap;
import java.util.Map;

import domain.accounts.Account;

/**
 * 
 * Class that manages account security
 * 
 */
public class SecuritySystem {

    private static SecuritySystem system;
    private static Map<Account, Integer> attempts;
    private static int maxAttempts;

    private SecuritySystem() { 
        attempts = new HashMap<>();
        maxAttempts = 3;
    }

    /**
     * 
     * Method that gets the instance of {@code SecuritySystem}
     * 
     * @return the instance of the {@code SecuritySystem}
     */
    public static SecuritySystem getInstance() {

        if (system == null) {
            system = new SecuritySystem();
        }

        return system;
    }

    /**
     *
     * Method that compares two given passwords
     *
     * @param password to check
     * @param checkPassword string to compare with
     *
     * @return True if both passwords are the same <p>
     *         False if they do not match
     *
     */
    public static boolean same(String password, String checkPassword) {

        return password.equals(checkPassword);
    }

    /**
     * 
     * Method that verifies a given password for an account <p>
     * If the {@code password} does not match with the account's one three times in a row, the account will be blocked temporarily
     * 
     * @param account to login
     * @param password to check
     * @return True if the {@code password} matches the password of the {@code account} <p>
     *         False in other cases
     */
    public boolean verify(Account account, String password) {

        Salt salt = account.getSalt();
        byte[] hashedPassword = account.getHashedPassword();

        boolean correct = Hasher.verify(password, salt, hashedPassword);

        if (!correct){
            addFailedLogin(account);
        } else {
            resetFailedLogin(account);
        }

        return correct;
    }

    /**
     * 
     * Method that adds a wrong login attempt to the {@code account}
     * 
     * @param account to add a wrong login attempt
     */
    private void addFailedLogin(Account account) {
        attempts.put(account, attempts.getOrDefault(account, 0) + 1);

        if (attempts.get(account) == maxAttempts) {
            blockAccount(account);
        }
    }

    /**
     * 
     * Method that blocks the {@code account} in case it reaches 3 wrong attempts
     * 
     * @param account to block temporarily
     */
    private void blockAccount(Account account) {
        account.setState(State.BLOCKED);
    }

    /**
     * 
     * Method that resets an {@code account} counter
     * 
     * @param account to reset
     */
    private void resetFailedLogin(Account account) {
        attempts.put(account, 0);
    }
}