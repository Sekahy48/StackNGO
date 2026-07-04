package dataAccessLayer.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EntryIdDAO {

    protected Connection connection = DBManager.getConnection();

    public int read() {
        String sql = "SELECT MAX(id) AS max_id\n" +
                "FROM (\n" +
                "    SELECT id FROM accounts\n" +
                "    UNION ALL\n" +
                "    SELECT id FROM items\n" +
                "    UNION ALL\n" +
                "    SELECT id FROM recipes\n" +
                "    UNION ALL\n" +
                "    SELECT id FROM collections\n" +
                "    UNION ALL\n" +
                "    SELECT id FROM component_definitions\n" +
                ") AS all_ids;";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("max_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}