package dataAccessLayer.DAO;

import java.sql.Connection;

import dataTransportLayer.GenericDTO;

public abstract class AbstractDAO<T extends GenericDTO, E> implements GenericDAO<T, E> {
    protected Connection connection = DBManager.getConnection(); 
}
