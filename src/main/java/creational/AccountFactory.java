package creational;

import dataTransportLayer.AccountDTO;
import domain.accounts.Account;
import logger.LogLevel;
import logger.Logger;

public class AccountFactory {
    public Account loadAccount(AccountDTO dto){
        if(dto.password == null || dto.hashedPass == null || dto.saltValue == null){

        }
        return new Account(dto.name, dto.collections, dto.type, dto.id, dto.hashedPass, dto.saltValue);
    }

    public Account createAccount(AccountDTO dto){
        if(!dto.password.isEmpty()){
            return new Account(dto.name, dto.password, dto.type, dto.id);
        }
        return null;
    }
}
