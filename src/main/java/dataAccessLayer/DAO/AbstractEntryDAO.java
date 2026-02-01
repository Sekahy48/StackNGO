package dataAccessLayer.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import dataTransportLayer.EntryDTO;
import logger.Logger;
import mvc.model.entries.Entry;

public abstract class AbstractEntryDAO<T extends EntryDTO, E extends Entry> extends AbstractDAO<T, E> implements ChildDAO<T> {

    /* ====== MÉTODOS QUE LAS HIJAS DEBEN DEFINIR ====== */

    protected abstract String getTableName();
    protected abstract T buildDTO(ResultSet rs) throws SQLException;
    protected abstract List<T> readAllInternal(int accountId) throws SQLException;

    /* ====== CRUD COMÚN ====== */

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            String error = "An error occurred trying to DELETE the Entry with id " + id + " from table " + getTableName() + " by query: " + sql;
            Logger.getInstance().error(this.getClass().toString(), error);
            return false;
        }
    }

    @Override
    public T read(int id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return buildDTO(rs);
            }
        } catch (SQLException e) {
            String error = "An error occurred trying to READ the Entry with id " + id + " from table " + getTableName() + " by query: " + sql;
            Logger.getInstance().error(this.getClass().toString(), error);
        }
        return null;
    }

    public T readByName(String name) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return buildDTO(rs);
            }
        } catch (SQLException e) {
            String error = "An error occurred trying to READ the Entry with name '" + name + "' from table " + getTableName() + " by query: " + sql;
            Logger.getInstance().error(this.getClass().toString(), error);
        }
        return null;
    }

    public boolean existsEntryByName(String name, int entryId, int collectionId) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE name = ? AND id != ? AND collection_id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setInt(2, entryId);
            stmt.setInt(3, collectionId);

            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            String error = "An error occurred trying to check existence of Entry with name '" + name +
                    "' (excluding id " + entryId + ") in collection " + collectionId + " from table " + getTableName();
            Logger.getInstance().error(this.getClass().toString(), error);
            return false;
        }
    }

    public boolean existsCollectionByName(String name, int entryId, int accountId) {
        String sql = "SELECT * FROM collections WHERE name = ? AND id != ? AND account_id = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setInt(2, entryId);
            stmt.setInt(3, accountId);

            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            String error = "An error occurred trying to check existence of Collection with name '" + name +
                    "' (excluding id " + entryId + ") for account " + accountId;
            Logger.getInstance().error(this.getClass().toString(), error);
            return false;
        }
    }

    public int getAccountIdByCollectionId(int collectionId) {
        String sql = "SELECT account_id FROM collections WHERE id = ?"; // añadí 'collections' que faltaba
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, collectionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("account_id");
            }
        } catch (SQLException e) {
            String error = "An error occurred trying to get account_id for collection with id " + collectionId;
            Logger.getInstance().error(this.getClass().toString(), error);
        }

        return -1;
    }

    public List<T> readAllByParent(int collectionId) {
        try {
            return readAllInternal(collectionId);
        } catch (SQLException e) {
            String error = "An error occurred trying to READ ALL entries for parent collection with id " + collectionId + " from table " + getTableName();
            Logger.getInstance().error(this.getClass().toString(), error);
            return List.of();
        }
    }


    
}
