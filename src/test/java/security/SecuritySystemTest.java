package security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.accounts.Account;
import identificators.AccountId;
import domain.accounts.AccountType;

public class SecuritySystemTest {

    private Account account;
    SecuritySystem system;

    @BeforeEach
    public void setUp() {
        this.account = new Account("Pepe", "prueba123", "USER", 1);
        system = SecuritySystem.getInstance();
    }

    @Test
    public void testCorrectVerify() {

        assertTrue(system.verify(this.account, "prueba123"));
    }

    @Test 
    public void testWrongVerify() {
        
        assertFalse(system.verify(this.account, "123prueba"));
        assertEquals(State.ALLOWED, this.account.getState());
        assertFalse(system.verify(this.account, "prueba_123"));
        assertEquals(State.ALLOWED, this.account.getState());
        assertFalse(system.verify(this.account, "no_se_me_ocurre"));
        assertEquals(State.BLOCKED, this.account.getState());
    }
}