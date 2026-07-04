package dataAccessLayer.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dataTransportLayer.ComponentDefinitionDTO;
import mvc.model.entries.component.ComponentDefinition;
import mvc.model.entries.component.ComponentField;
import mvc.model.entries.component.FieldType; 

public class ComponentDefinitionDAO extends AbstractEntryDAO<ComponentDefinitionDTO, ComponentDefinition> {

    @Override
    protected String getTableName() {
        return "component_definitions";
    }

    @Override
    protected ComponentDefinitionDTO buildDTO(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        return new ComponentDefinitionDTO(
            rs.getString("name"),
            rs.getString("icon"),
            rs.getString("description"),
            id,
            readFields(id)
        );
    }

    @Override
    protected List<ComponentDefinitionDTO> readAllInternal(int parentId) throws SQLException {
        String sql = "SELECT * FROM component_definitions WHERE account_id = ?";
        List<ComponentDefinitionDTO> out = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, parentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) out.add(buildDTO(rs));
        }
        return out;
    }

    @Override
    public boolean create(ComponentDefinition entry, int[] foreignKeys) {
        String sql = "INSERT INTO component_definitions (id, name, description, icon, account_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, entry.getId().value());
            stmt.setString(2, entry.getName());
            stmt.setString(3, entry.getDescription());
            stmt.setString(4, entry.getImagePath());
            stmt.setInt(5, foreignKeys[0]);
            boolean ok = stmt.executeUpdate() > 0;
            if (ok) insertFields(entry.getId().value(), entry.getFields());
            return ok;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(ComponentDefinition entry, int id) {
        String sql = "UPDATE component_definitions SET name = ?, description = ?, icon = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, entry.getName());
            stmt.setString(2, entry.getDescription());
            stmt.setString(3, entry.getImagePath());
            stmt.setInt(4, id);
            boolean ok = stmt.executeUpdate() > 0;
            if (ok) updateFields(id, entry.getFields());
            return ok;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateFields(int defId, List<ComponentField> fields) {
        String deleteSql = "DELETE FROM component_fields WHERE component_def_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteSql)) {
            stmt.setInt(1, defId);
            stmt.executeUpdate();
            insertFields(defId, fields);
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ComponentField> readFields(int defId) throws SQLException {
        String sql = "SELECT field_name, field_type, enum_values FROM component_fields WHERE component_def_id = ?";
        List<ComponentField> fields = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, defId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String enumRaw = rs.getString("enum_values");
                List<String> enumValues = (enumRaw == null || enumRaw.isBlank())
                        ? new ArrayList<>()
                        : new ArrayList<>(java.util.Arrays.asList(enumRaw.split(",")));
                fields.add(new ComponentField(
                    rs.getString("field_name"),
                    FieldType.valueOf(rs.getString("field_type")),
                    enumValues
                ));
            }
        }
        return fields;
    }

    private void insertFields(int defId, List<ComponentField> fields) throws SQLException {
        String sql = "INSERT INTO component_fields (component_def_id, field_name, field_type, enum_values) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (ComponentField f : fields) {
                stmt.setInt(1, defId);
                stmt.setString(2, f.getFieldName());
                stmt.setString(3, f.getFieldType().name());
                stmt.setString(4, f.getEnumValues().isEmpty() ? null : String.join(",", f.getEnumValues()));
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
}