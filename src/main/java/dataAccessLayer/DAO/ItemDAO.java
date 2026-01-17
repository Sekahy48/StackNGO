package dataAccessLayer.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import creational.DTOFactory;
import dataTransportLayer.ItemDTO;
import mvc.model.entries.Item;

public class ItemDAO extends AbstractEntryDAO<ItemDTO, Item> {

    @Override
    protected String getTableName() {
        return "items";
    }

    @Override
    protected ItemDTO buildDTO(ResultSet rs) throws SQLException {
        return DTOFactory.item(
            rs.getString("name"),
            rs.getString("icon"),
            rs.getString("description"),
            rs.getInt("id")
        );
    }

    @Override
    protected List<ItemDTO> readAllInternal(int collectionId) throws SQLException {
        List<ItemDTO> out = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE collection_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, collectionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                out.add(buildDTO(rs));
            }
        }
        return out;
    }

    public int isInRecipe(int itemId) {
        int recipeId;
        String sql = "SELECT recipes_id FROM recipe_inputs WHERE items_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();
            recipeId = rs.next() ? rs.getInt("recipes_id") : -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return recipeId;

    }

    @Override
    public boolean create(Item entry, int[] foreignKeys) {
        String sql = "INSERT INTO items (id, name, icon, description, collection_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, entry.getId().value());
            stmt.setString(2, entry.getName());
            stmt.setString(3, entry.getImagePath());
            stmt.setString(4, entry.getDescription());
            stmt.setInt(5, foreignKeys[0]); // collection_id
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(Item entry, int id) {
        String sql = "UPDATE items SET name = ?, icon = ?, description = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, entry.getName());
            stmt.setString(2, entry.getImagePath());
            stmt.setString(3, entry.getDescription());
            stmt.setInt(4, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
