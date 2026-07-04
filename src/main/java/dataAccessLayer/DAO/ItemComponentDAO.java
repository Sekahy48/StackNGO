package dataAccessLayer.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mvc.model.entries.component.ItemComponentValue;

public class ItemComponentDAO {

    protected Connection connection = DBManager.getConnection();

    public List<ItemComponentValue> readByItem(int itemId) {
        String sql = "SELECT component_def_id, field_name, field_value FROM item_components WHERE item_id = ?";
        Map<Integer, ItemComponentValue> byDefId = new HashMap<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int defId = rs.getInt("component_def_id");
                ItemComponentValue value = byDefId.computeIfAbsent(defId, ItemComponentValue::new);
                value.setValue(rs.getString("field_name"), rs.getString("field_value"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new ArrayList<>(byDefId.values());
    }

    public void updateForItem(int itemId, List<ItemComponentValue> components) {
        String deleteSql = "DELETE FROM item_components WHERE item_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteSql)) {
            stmt.setInt(1, itemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        insertForItem(itemId, components);
    }

    private void insertForItem(int itemId, List<ItemComponentValue> components) {
        String sql = "INSERT INTO item_components (item_id, component_def_id, field_name, field_value) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (ItemComponentValue value : components) {
                for (Map.Entry<String, String> entry : value.getFieldValues().entrySet()) {
                    stmt.setInt(1, itemId);
                    stmt.setInt(2, value.getComponentDefId());
                    stmt.setString(3, entry.getKey());
                    stmt.setString(4, entry.getValue());
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteForItem(int itemId) {
        String sql = "DELETE FROM item_components WHERE item_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}