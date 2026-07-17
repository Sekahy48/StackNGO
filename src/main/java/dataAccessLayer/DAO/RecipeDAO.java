package dataAccessLayer.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import creational.DTOFactory;
import dataTransportLayer.ItemDTO;
import dataTransportLayer.ItemIdStackDTO;
import dataTransportLayer.ItemStackDTO;
import dataTransportLayer.RecipeDTO;
import mvc.model.entries.ItemIdStack;
import mvc.model.entries.Recipe; 

public class RecipeDAO extends AbstractEntryDAO<RecipeDTO, Recipe> {

    @Override
    protected String getTableName() {
        return "recipes";
    }

    @Override
    protected RecipeDTO buildDTO(ResultSet rs) throws SQLException {
        int recipeId = rs.getInt("id");

        // Rellenar ingredients
        List<ItemIdStackDTO> ingredients = new ArrayList<>();
        String sqlInputs = "SELECT items_id, quantity FROM recipe_inputs WHERE recipes_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlInputs)) {
            stmt.setInt(1, recipeId);
            ResultSet rsInputs = stmt.executeQuery();
            while (rsInputs.next()) {
                ingredients.add(DTOFactory.itemIdStack(
                    rsInputs.getInt("items_id"),
                    rsInputs.getInt("quantity")
                ));
            }
        }

        // Rellenar results
        List<ItemIdStackDTO> results = new ArrayList<>();
        String sqlOutputs = "SELECT items_id, quantity FROM recipe_outputs WHERE recipes_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlOutputs)) {
            stmt.setInt(1, recipeId);
            ResultSet rsOutputs = stmt.executeQuery();
            while (rsOutputs.next()) {
                results.add(DTOFactory.itemIdStack(
                    rsOutputs.getInt("items_id"),
                    rsOutputs.getInt("quantity")
                ));
            }
        }

        return DTOFactory.recipe(
            new ArrayList<>(ingredients),
            new ArrayList<>(results),
            rs.getString("name"),
            rs.getString("icon"),
            rs.getString("description"),
            recipeId
        );
    }

    @Override
    protected List<RecipeDTO> readAllInternal(int collectionId) throws SQLException {
        List<RecipeDTO> out = new ArrayList<>();
        String sql = "SELECT * FROM recipes WHERE collection_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, collectionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                out.add(buildDTO(rs));
            }
        }
        return out;
    }

    public List<ItemStackDTO> getInputs(int recipeId) {
        List<ItemStackDTO> inputs = new ArrayList<>();
        String sql = "SELECT * FROM recipe_inputs WHERE recipes_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ItemDAO itemDAO = new ItemDAO();
                ItemDTO dto = itemDAO.read(rs.getInt("items_id"));
                inputs.add(DTOFactory.itemStack(dto, rs.getInt("quantity")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return inputs;
    }

    public List<ItemStackDTO> getOutputs(int recipeId) {
        List<ItemStackDTO> outputs = new ArrayList<>();
        String sql = "SELECT * FROM recipe_outputs WHERE recipes_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ItemDAO itemDAO = new ItemDAO();
                ItemDTO dto = itemDAO.read(rs.getInt("items_id"));
                outputs.add(DTOFactory.itemStack(dto, rs.getInt("quantity")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return outputs;
    }

    @Override
    public boolean create(Recipe entry, int[] foreignKeys) {
        String sql = "INSERT INTO recipes (id, name, icon, description, collection_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, entry.getId().value());
            stmt.setString(2, entry.getName());
            stmt.setString(3, entry.getImagePath());
            stmt.setString(4, entry.getDescription());
            stmt.setInt(5, foreignKeys[0]);

            boolean recipe = stmt.executeUpdate() > 0;
            if (!recipe) return false;

            insertInputs(entry, foreignKeys[0]);
            insertOutputs(entry, foreignKeys[0]);

            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method that inserts all the inputs linked to a recipe.
     * @param recipe
     * @param collectionId
     */
    private void insertInputs(Recipe recipe, int collectionId) {
        for (ItemIdStack stack : recipe.getIngredients()) {
            insertSingleInput(recipe.getId().value(), stack.getId().value(), stack.getAmount(), collectionId);
        }
    }

    public void insertSingleInput(int recipeId, int itemId, int amount, int collectionId) {
        String sql = "INSERT INTO recipe_inputs (recipes_id, items_id, quantity, collection_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            stmt.setInt(2, itemId);
            stmt.setInt(3, amount);
            stmt.setInt(4, collectionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteSingleInput(int recipeId, int itemId) {
        String sql = "DELETE FROM recipe_inputs WHERE recipes_id = ? AND items_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            stmt.setInt(2, itemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateInputAmount(int recipeId, int itemId, int amount) {
        String sql = "UPDATE recipe_inputs SET quantity = ? WHERE recipes_id = ? AND items_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, amount);
            stmt.setInt(2, recipeId);
            stmt.setInt(3, itemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertOutputs(Recipe entry, int collectionId) {
        for (ItemIdStack stack : entry.getResults()) {
            insertSingleOutput(entry.getId().value(), stack.getId().value(), stack.getAmount(), collectionId);
        }
    }

    public void insertSingleOutput(int recipeId, int itemId, int amount, int collectionId) {
        String sql = "INSERT INTO recipe_outputs (recipes_id, items_id, quantity, collection_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            stmt.setInt(2, itemId);
            stmt.setInt(3, amount);
            stmt.setInt(4, collectionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteSingleOutput(int recipeId, int itemId) {
        String sql = "DELETE FROM recipe_outputs WHERE recipes_id = ? AND items_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            stmt.setInt(2, itemId);
            //stmt.setInt(3, collectionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateOutputAmount(int recipeId, int itemId, int amount) {
        String sql = "UPDATE recipe_outputs SET quantity = ? WHERE recipes_id = ? AND items_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, amount);
            stmt.setInt(2, recipeId);
            stmt.setInt(3, itemId);
            //stmt.setInt(4, collectionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean update(Recipe entry, int id) {
        String sql = "UPDATE recipes SET name = ?, icon = ?, description = ? WHERE id = ?";
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

    @Override
    protected String getParentColumnName() {
        return "collection_id";    
    }
}