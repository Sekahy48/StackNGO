package dataAccessLayer.DAO;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
                "CREATE TABLE IF NOT EXISTS component_definitions (" +
                "id INT PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +   // sin UNIQUE global
                "description VARCHAR(500)," +
                "icon VARCHAR(255)," +
                "account_id INT NOT NULL, " +
                "FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE, " +
                "UNIQUE KEY unique_def_per_account (name, account_id)" +
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

            // Magnitud que decide que etapa de modelo se muestra. Vive en el item y no en
            // una tabla aparte porque es un unico valor y sin etapas no significa nada.
            ensureColumn("items", "model_driven_by", "VARCHAR(200) NULL");

            // Etapas de modelo: un umbral de la magnitud elegida y las variantes que
            // corresponden a ese umbral.
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS item_model_stages (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "item_id INT NOT NULL, " +
                "threshold FLOAT NOT NULL, " +
                "FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE, " +
                "UNIQUE KEY unique_threshold_per_item (item_id, threshold)" +
                ");"
            );

            // Ficheros de una etapa. stored_name es el nombre con el que vive en la carpeta
            // de la aplicacion; original_name es el que eligio el autor y solo sirve de
            // etiqueta en el editor.
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS item_model_files (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "stage_id INT NOT NULL, " +
                "stored_name VARCHAR(255) NOT NULL, " +
                "original_name VARCHAR(255) NOT NULL, " +
                "sort_order INT NOT NULL DEFAULT 0, " +
                "FOREIGN KEY (stage_id) REFERENCES item_model_stages(id) ON DELETE CASCADE" +
                ");"
            );

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Anade una columna a una tabla ya existente solo si todavia no esta.
     *
     * <p>El arranque crea el esquema con CREATE TABLE IF NOT EXISTS, que es idempotente
     * pero no actualiza tablas que ya existen: una columna nueva sobre una base de datos
     * en uso necesita un ALTER, y un ALTER a secas falla en el segundo arranque. Consultar
     * information_schema antes deja la operacion repetible igual que el resto del
     * arranque.</p>
     *
     * @param table tabla destino
     * @param column columna a garantizar
     * @param definition tipo y modificadores, tal como irian tras el nombre en un ALTER
     */
    private static void ensureColumn(String table, String column, String definition) throws SQLException {
        String check = "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                       "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?";

        try (PreparedStatement stmt = conn.prepareStatement(check)) {
            stmt.setString(1, table);
            stmt.setString(2, column);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return;
        }

        try (Statement alter = conn.createStatement()) {
            alter.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
        }
    }

    public static Connection getConnection() {
        if (conn == null) {
            initFromConfig();
        }
        return conn;
    }
}