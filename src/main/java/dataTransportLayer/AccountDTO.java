package dataTransportLayer;

import logger.LogLevel;
import logger.Logger;

import java.util.List;

public class AccountDTO implements GenericDTO {

    public String name;  
    public List<Integer> collections;
    public String type;
    public int id;
    public byte[] hashedPass;
    public byte[] saltValue;
    public String password;

    /**
     * 
     * Constructor of a DTO for the user
     * 
     * @param name of the user
     * @param collections the user has
     * @param type of account the user has
     * 
     */
    public AccountDTO(String name, List<Integer> collections, String type, int id, byte[] hashedPass, byte[]saltValue) {

        this.name = name;
        this.collections = collections;
        this.type = type;
        this.id = id;
        this.hashedPass = hashedPass;
        this.saltValue = saltValue;
    }

    public AccountDTO(String name, String password, String type, int id){
        this.name = name;
        this.password = password;
        this.type = type;
        this.id = id;
    }

    @Override
    public String getIconPath() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
}