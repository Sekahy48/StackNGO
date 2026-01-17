package account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.accounts.Account;
import identificators.AccountId;
import domain.accounts.AccountType;
import security.SecuritySystem;

public class AccountTest {
    
    private Account account;

    @BeforeEach
    public void setUp() {

        this.account = new Account("Pepe", 
            "123", "USER", 1
        );

        SecuritySystem system = SecuritySystem.getInstance();
    }

    @Test
    public void testGetUserName() {
        assertEquals("Pepe", this.account.getUsername());
    }

    @Test
    public void testGetType() {
        assertEquals(AccountType.USER, this.account.getType());
    }

    @Test
    public void testGetContent() {
        assertEquals(new ArrayList<>(), this.account.getContent());
    }

    @Test
    public void testGetSalt() {
        assertNotNull(this.account.getSalt());
    }

    @Test
    public void testGetHashedPassword() {
        assertNotNull(this.account.getHashedPassword());
    }

    @Test
    public void testChangePassword() {

        byte[] currentPassword = this.account.getHashedPassword();
        this.account.changePassword("234");
        byte[] newPassword = this.account.getHashedPassword();

        assertNotEquals(currentPassword, newPassword);
    }

    @Test
    public void testVerify() {
        
        assertTrue(this.account.verify("123"));
        assertFalse(this.account.verify("234"));
    }
}
