package dataAccessLayer.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import creational.DTOFactory;
import dataTransportLayer.CollectionDTO;
import mvc.model.entries.Collection;

public class CollectionDAO extends AbstractEntryDAO<CollectionDTO, Collection> {

    @Override
    protected String getTableName() {
        return "collections";
    }

    @Override
    protected CollectionDTO buildDTO(ResultSet rs) throws SQLException {
        int collectionId = rs.getInt("id");

        // Rellenar items
        List<Integer> items = new ArrayList<>();
        String sqlItems = "SELECT id FROM items WHERE collection_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlItems)) {
            stmt.setInt(1, collectionId);
            ResultSet rsItems = stmt.executeQuery();
            while (rsItems.next()) {
                items.add(rsItems.getInt("id"));
            }
        }

        // Rellenar recipes
        List<Integer> recipes = new ArrayList<>();
        String sqlRecipes = "SELECT id FROM recipes WHERE collection_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlRecipes)) {
            stmt.setInt(1, collectionId);
            ResultSet rsRecipes = stmt.executeQuery();
            while (rsRecipes.next()) {
                recipes.add(rsRecipes.getInt("id"));
            }
        }

        return DTOFactory.collection(
            new ArrayList<>(items),
            new ArrayList<>(recipes),
            rs.getString("name"),
            rs.getString("icon"),
            rs.getString("description"),
            collectionId
        );
    }


    @Override
    protected List<CollectionDTO> readAllInternal(int accountId) throws SQLException {
        List<CollectionDTO> out = new ArrayList<>();
        String sql = "SELECT * FROM collections WHERE account_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                out.add(buildDTO(rs));
            }
        }
        return out;
    }

    @Override
    public boolean create(Collection entry, int[] foreignKeys) {
        String sql = "INSERT INTO collections (id, name, description, icon, account_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, entry.getId().value());
            stmt.setString(2, entry.getName());
            stmt.setString(3, entry.getDescription());
            stmt.setString(4, entry.getImagePath());
            stmt.setInt(5, foreignKeys[0]);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(Collection entry, int id) {
        String sql = "UPDATE collections SET name = ?, description = ?, icon = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, entry.getName());
            stmt.setString(2, entry.getDescription());
            stmt.setString(3, entry.getImagePath());
            stmt.setInt(4, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
