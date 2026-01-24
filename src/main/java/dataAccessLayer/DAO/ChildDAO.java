package dataAccessLayer.DAO;

import java.util.List;

import dataTransportLayer.GenericDTO;

public interface ChildDAO<T extends GenericDTO> {
    List<T> readAllByParent(int parentId);
}
