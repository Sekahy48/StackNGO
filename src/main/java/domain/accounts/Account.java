package domain.accounts;

import java.util.ArrayList;
import java.util.List;

import identificators.AccountId;
import identificators.EntryId;
import security.Hasher;
import security.Salt;
import security.SecuritySystem;
import security.State;

public class Account {

    private final String username;
    private byte[] hashedPassword;
    private final List<EntryId> content;
    private AccountType type;
    private State state;
    private Salt salt;
    private SecuritySystem security;
    private AccountId id;

    /**
     * Constructor for new accounts, UI generated
     * @param username
     * @param password
     * @param type
     * @param id
     */
    public Account(String username, String password, String type, int id) {
        this.id = new AccountId(id);
        this.salt = Salt.generate();
        this.username = username;
        this.hashedPassword = Hasher.hash(password, this.salt);
        this.type = AccountType.valueOf(type.toUpperCase());
        this.content = new ArrayList<>();
        this.state = State.ALLOWED;
        this.security = SecuritySystem.getInstance();
    }

    public Account(String username, List<Integer> collections, String type, int id, byte[] hashedPass, byte[] saltValue){
        this.username = username;
        List<EntryId> collIds = new ArrayList<>();
        for (Integer elem : collections) {
            collIds.add(new EntryId(elem));
        }
        this.content = collIds;
        this.type = AccountType.valueOf(type.toUpperCase());
        this.id = new AccountId(id);
        this.hashedPassword = hashedPass;
        this.salt = new Salt(saltValue);
        this.security = SecuritySystem.getInstance();
    } 

    public void changePassword(String password) {

        this.salt = Salt.generate();
        this.hashedPassword = Hasher.hash(password, this.salt);
    }

    public boolean verify(String anotherPassword) {

        return security.verify(this, anotherPassword);
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return this.state;
    }

    public String getUsername() {
        return this.username;
    }

    public AccountType getType() {
        return this.type;
    }

    public List<EntryId> getContent() {
        return List.copyOf(content);
    }

    /* package-private: solo servicios de dominio/auth */
    public byte[] getHashedPassword() {
        return this.hashedPassword;
    }

    public Salt getSalt() {
        return this.salt;
    }

    public AccountId getId(){
        return this.id;
    }
}