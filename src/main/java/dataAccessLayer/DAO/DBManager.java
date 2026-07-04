package dataAccessLayer.DAO;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBManager {

    private static Connection conn;

    private static void initFromConfig() {

        Properties props = new Properties();
        try (InputStream input = DBManager.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (input == null) { 
                return;
            }

            props.load(input);

            conn = DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.user"),
                    props.getProperty("db.password")
            );

            Statement stmt = conn.createStatement();

            // Cuentas  
            stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS accounts (" +
                    "id INT PRIMARY KEY, " +
                    "name VARCHAR (100) NOT NULL UNIQUE," +
                    "password VARBINARY(64) NOT NULL," +
                    "salt VARBINARY(64) NOT NULL," +
                    "type VARCHAR(50) NOT NULL);"
            );

            // Colecciones
            stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS collections (" +
                    "id INT PRIMARY KEY, " +
                    "name VARCHAR (100) NOT NULL," +
                    "icon VARCHAR(255)," +
                    "description VARCHAR(500)," +
                    "account_id INT NOT NULL," +
                    "FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE," +
                    "UNIQUE KEY unique_collection_per_account (name, account_id)" +
                    ");"

            );

            // Recetas
            stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS recipes (" +
                    "id INT PRIMARY KEY, " +
                    "name VARCHAR (100) NOT NULL," +
                    "icon VARCHAR(255)," +
                    "description VARCHAR(500)," +
                    "collection_id INT NOT NULL, " +
                    "FOREIGN KEY (collection_id) REFERENCES collections(id) ON DELETE CASCADE," +
                    "UNIQUE KEY unique_recipe_per_collection (name, collection_id)" +
                    ");"
            );

            // Items
            stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS items (" +
                    "id INT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "icon VARCHAR(255), " +
                    "description VARCHAR(500), " +
                    "collection_id INT NOT NULL, " +
                    "FOREIGN KEY (collection_id) REFERENCES collections(id) ON DELETE CASCADE," +
                    "UNIQUE KEY unique_item_per_collection (name, collection_id)" +
                    ");"
            ); 

            // io_type: INPUT o OUTPUT
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS recipe_io ( " +
                "recipes_id INT NOT NULL, " +
                "items_id INT NOT NULL, " +
                "quantity INT NOT NULL, " +
                "io_type VARCHAR(6) NOT NULL, " +
                "PRIMARY KEY (recipes_id, items_id, io_type), " +
                "FOREIGN KEY (recipes_id) REFERENCES recipes(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (items_id) REFERENCES items(id) ON DELETE CASCADE " +
                ");");
        
            // Componentes        
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS component_definitions (" +
                "id INT PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL UNIQUE," +
                "description VARCHAR(500)," +
                "icon VARCHAR(255)," +
                "account_id INT NOT NULL, " +
                "FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE, " +
                "UNIQUE KEY unique_def_per_account (name, account_id)" +
                ");"
            );

            // Campos de componentes
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS component_fields (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "component_def_id INT NOT NULL," +
                "field_name VARCHAR(100) NOT NULL," +
                "field_type VARCHAR(50) NOT NULL," +
                "enum_values VARCHAR(500)," +
                "FOREIGN KEY (component_def_id) REFERENCES component_definitions(id) ON DELETE CASCADE," +
                "UNIQUE KEY unique_field_per_def (component_def_id, field_name)" +
                ");"
            );

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS item_components (" +
                "item_id INT NOT NULL, " +
                "component_def_id INT NOT NULL, " +
                "field_name VARCHAR(100) NOT NULL, " +
                "field_value VARCHAR(255) NOT NULL, " +
                "PRIMARY KEY (item_id, component_def_id, field_name), " +
                "FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (component_def_id) REFERENCES component_definitions(id) ON DELETE CASCADE" +
                ");"
            );


        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() {
        if (conn == null) {
            initFromConfig();
        }
        return conn;
    }
}