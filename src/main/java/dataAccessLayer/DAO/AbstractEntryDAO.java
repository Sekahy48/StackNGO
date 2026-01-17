package dataAccessLayer.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
 
import dataTransportLayer.GenericDTO;
import mvc.model.entries.Entry;

public abstract class AbstractEntryDAO<T extends GenericDTO, E extends Entry> extends AbstractDAO<T, E> {

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
        } catch (SQLException ignored) {}
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
        } catch (SQLException ignored) {}
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
                    throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }



    public int getAccountIdByCollectionId(int collectionId) {
        String sql = "SELECT account_id FROM  WHERE id = ?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, collectionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("account_id");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return -1;
    }

    @Override
    public List<T> readAll(int collectionId) {
        try {
            return readAllInternal(collectionId);
        } catch (SQLException e) {
            return List.of();
        }
    }
}
