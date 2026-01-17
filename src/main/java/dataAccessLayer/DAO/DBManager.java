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

            // Inicializar conexión
            conn = DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.user"),
                    props.getProperty("db.password")
            );

            Statement stmt = conn.createStatement();

            // Creación de tablas (solo se crean en el caso de que no existan)

            // Cuentas (probablemente cambie que el id sea autoincrementable)
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
                    "FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE," + // ON DELETE CASCADE se utiliza para que si se borra una cuenta
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
                    "FOREIGN KEY (collection_id) REFERENCES collections(id) ON DELETE CASCADE," + // ON DELETE CASCADE se utiliza para que si se borra una colección, sus items también seran eliminados
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
                    "FOREIGN KEY (collection_id) REFERENCES collections(id) ON DELETE CASCADE," + // ON DELETE CASCADE se utiliza para que si se borra una colección, sus ítems también seran eliminados
                    "UNIQUE KEY unique_item_per_collection (name, collection_id)" +
                    ");"
            );

            // Recipe inputs
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS recipe_inputs(" +
                    "quantity INT," +
                    "collection_id INT NOT NULL, " +
                    "recipes_id INT NOT NULL, " +
                    "items_id INT NOT NULL, " +
                    "PRIMARY KEY (recipes_id, items_id), " +
                    "FOREIGN KEY (recipes_id)  REFERENCES recipes(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (items_id)  REFERENCES items(id) ON DELETE CASCADE," +
                    "UNIQUE KEY unique_input_per_recipe (items_id, recipes_id)" +
                    ");"
            );

            // Recipe outputs
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS recipe_outputs(" +
                    "quantity INT," +
                    "collection_id INT NOT NULL, " +
                    "recipes_id INT NOT NULL, " +
                    "items_id INT NOT NULL, " +
                    "PRIMARY KEY (recipes_id, items_id), " +
                    "FOREIGN KEY (recipes_id)  REFERENCES recipes(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (items_id)  REFERENCES items(id) ON DELETE CASCADE," +
                    "UNIQUE KEY unique_output_per_recipe (items_id, recipes_id)" +
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
