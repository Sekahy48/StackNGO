package mvc.model.entries.repository;

import dataAccessLayer.DAO.EntryIdDAO;

public class EntryIdGenerator {
    private static EntryIdGenerator instance;
    private int lastId;
    private static EntryIdDAO entryIdDAO;

    public static EntryIdGenerator getInstance(){
        if(instance == null){
            instance = new EntryIdGenerator();
            entryIdDAO = new EntryIdDAO();
        }

        return instance;
    }

    public int generateId(){

        int maxId = entryIdDAO.read();

        if (maxId > lastId){
            lastId = maxId;
        }

        this.lastId++;
        return this.lastId;
    }

    public void setLastId(int id){
        this.lastId = id;
    }
}
