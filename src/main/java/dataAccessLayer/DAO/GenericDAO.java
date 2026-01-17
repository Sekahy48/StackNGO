package dataAccessLayer.DAO;

import java.util.List;

import dataTransportLayer.GenericDTO; 

public interface GenericDAO<T extends GenericDTO, E> {
    public boolean create(E entry, int[] foreignKeys);
    public boolean delete(int id);
    public boolean update(E entry, int id);
    public T read(int id);
    public List<T> readAll(int accountID);
}

