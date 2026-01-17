 package command.modify;

import command.ICommand;
import command.modify.modifiers.*;
import command.screen.ChangeScreenCommand;
import command.screen.RedirectCommand;
import command.show.ShowCollection;
import command.show.ShowItem;
import command.show.ShowRecipe;
import dataAccessLayer.DAO.DAOType;
import dataAccessLayer.DAO.CollectionDAO;
import dataTransportLayer.EntryDTO;
import dataTransportLayer.EventBuffer;
import identificators.EntryId;
import javafx.scene.control.Alert;
import logger.LogLevel;
import logger.Logger;
import mvc.controller.AbstractController;
import mvc.controller.modify.ModType;
import mvc.view.ViewType;

import java.util.HashMap;
import java.util.Map;

public class ModifyEntryCommand implements ICommand {

    private final EntryDTO dto;
    private final EntryModifier modifier;
    private final ModType modType;

    private Map<ModType, ViewType> typesMap = new HashMap<ModType, ViewType>(){
        {
            put(ModType.ITEM, ViewType.SHOW_ITEM);
            put(ModType.COLLECTION, ViewType.SHOW_COLLECTION);
            put(ModType.RECIPE, ViewType.SHOW_RECIPE);
        }
    };
    private static final Map<ModType, EntryModifier> modifierMap = Map.of(
        ModType.ITEM, new ItemModifier(),
        ModType.COLLECTION, new CollectionModifier(),
        ModType.RECIPE, new RecipeModifier()
    );

    public ModifyEntryCommand(EntryDTO dto, ModType modType) {
        this.dto = dto;
        this.modifier = modifierMap.get(modType);
        this.modType = modType;
        if (modifier == null) 
            throw new IllegalArgumentException("Tipo desconocido: " + modType);
    }

    @Override
    public void execute(AbstractController controller) {
 
        modifier.modify(dto, controller);

        String log =
                modType.getText() +
                        " con nombre " + dto.name +
                        (modType == ModType.COLLECTION
                                ? " ha sido modificada."
                                : " en la coleccion " +
                                controller.getRuntimeContext().getCurrentCollection().name +
                                " ha sido modificada.");

        String alert = modType.getText() + " con nombre " + dto.name + " ha sido modificada.";

        controller.getView().showAlert("Modificacion", alert, Alert.AlertType.INFORMATION);
        Logger.getInstance().log(LogLevel.INFO, this.getClass().toString(), log);

        ICommand showCommand;
        switch (modType) {
            case COLLECTION -> showCommand = new ShowCollection(controller.getRuntimeContext().getCollectionById(dto.id));
            case ITEM -> showCommand = new ShowItem(controller.getRuntimeContext().getItemDTOById(dto.id).id);
            case RECIPE -> showCommand = new ShowRecipe(controller.getRuntimeContext().getRecipeDTOById(dto.id));
            default -> throw new IllegalArgumentException("Tipo desconocido: " + modType);
        }

        EventBuffer buffer;
        switch (modType) {
            case COLLECTION -> buffer = controller.getRuntimeContext().getCoreController().getShowCollectionDataBuffer();
            case ITEM -> buffer = controller.getRuntimeContext().getCoreController().getShowItemDataBuffer();
            case RECIPE -> buffer = controller.getRuntimeContext().getCoreController().getShowRecipeDataBuffer();
            default -> throw new IllegalArgumentException("Tipo desconocido: " + modType);
        }        controller.getBuffer().publish(new RedirectCommand(buffer, showCommand));
        controller.getBuffer().publish(new ChangeScreenCommand(typesMap.get(modType)));
    }

    @Override
    public void clear() {

    }
     
}
