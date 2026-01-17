package command.modify.modifiers;

import dataAccessLayer.DAO.DAOType;
import dataAccessLayer.DAO.RecipeDAO;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.RecipeDTO; 
import mvc.context.RuntimeContext;
import mvc.controller.AbstractController;
import mvc.model.entries.Recipe;
import mvc.model.entries.repository.EntriesRepository;

public class RecipeModifier implements EntryModifier {
    @Override
    public void modify(EntryDTO dto, AbstractController controller) {
        // Lógica para añadir/modificar un recipe
        RuntimeContext context = controller.getRuntimeContext();
        EntriesRepository repo = context.getRepo(); 
        Recipe recipe = context.getEntriesFactory().createRecipe((RecipeDTO) dto);

        ((RecipeDAO) context.getDAO(DAOType.RECIPE)).update(recipe, recipe.getId().value());

        if (!repo.contains(recipe.getId())){
            repo.addRecipe(recipe);
        }else{
            repo.modifyEntry(recipe);
        }
    }
}
