package mvc.controller.show.entry.data;

import java.util.List;

import command.delete.DeleteRecipeCommand;
import command.modify.screen.ModifyRecipeCommand;
import command.screen.ChangeScreenCommand;
import command.screen.RedirectCommand;
import command.show.ShowCollection;
import creational.UIPrefabsFactory;
import static creational.UIPrefabsFactory.addPopUp;
import static creational.UIPrefabsFactory.rowExists;
import dataAccessLayer.DAO.DAOType;
import dataAccessLayer.DAO.ItemDAO;
import dataAccessLayer.DAO.RecipeDAO;
import dataTransportLayer.EventBuffer;
import dataTransportLayer.ItemDTO;
import dataTransportLayer.ItemStackDTO;
import dataTransportLayer.RecipeDTO;
import identificators.EntryId;
import identificators.GenericId;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import logger.LogLevel;
import logger.Logger;
import mvc.model.entries.RecipeIOType;
import mvc.view.ViewType;
import mvc.view.show.entry.data.ShowRecipeDataView;
import utilities.ImageUtils;

public class ShowRecipeDataController extends AbstractShowDataController<ShowRecipeDataView> {

    public ShowRecipeDataController(EventBuffer buffer) {
        super(buffer);
    }

    @Override
    public void handleButton() {

        commonHandleButton();

        ShowRecipeDataView view = this.getView();

        Button deleteButton = view.getDeleteButton();
        Button modifyButton = view.getModifyButton();
        Button goBackButton = view.getGoBackButton();
        Button addOutputButton = view.getAddOutputButton();
        Button addInputButton = view.getAddInputButton();

        deleteButton.setOnAction(
                e -> {
                    GenericId id = this.view.getParentId();
                    this.buffer.publish(new DeleteRecipeCommand((EntryId) id));
                }
        );

        modifyButton.setOnAction(
                e -> {
                    this.buffer.publish(new ChangeScreenCommand(ViewType.MODIFY_RECIPE));
                    this.buffer.publish(new RedirectCommand(context.getSystemContext().getController(ViewType.MODIFY_RECIPE).getBuffer(),
                                                            new ModifyRecipeCommand(context.getRecipeDTOById(this.view.getParentId().value()))
                                                            ));
                }
        );

        goBackButton.setOnAction(
                e -> {
                    goBack();
                }
        );

        addInputButton.setOnAction(
                e -> {
                    ItemDAO itemDAO = (ItemDAO) this.context.getDAO(DAOType.ITEM);
                    RecipeDAO recipeDAO = (RecipeDAO) this.context.getDAO(DAOType.RECIPE);
                    List<ItemDTO> items = itemDAO.readAllByParent(this.context.getSessionContext().getCurrentCollection().getId());
                    addPopUp(
                            addInputButton,
                            items,
                            item -> addInput(item, view.getInputList(), recipeDAO));
                }
        );

        addOutputButton.setOnAction(
                e -> {
                    ItemDAO itemDAO = (ItemDAO) this.context.getDAO(DAOType.ITEM);
                    RecipeDAO recipeDAO = (RecipeDAO) this.context.getDAO(DAOType.RECIPE);

                    List<ItemDTO> items = itemDAO.readAllByParent(this.context.getSessionContext().getCurrentCollection().getId());

                    addPopUp(
                            addOutputButton,
                            items,
                            item -> addOutput(item, view.getOutputList(), recipeDAO));
                }
        );
    }

    private void fillItems(Integer id) {
        VBox input = view.getInputList();
        VBox output = view.getOutputList();

        input.getChildren().clear();
        output.getChildren().clear();

        RecipeDAO recipeDAO = (RecipeDAO) this.context.getDAO(DAOType.RECIPE);

        List<ItemStackDTO> listaInput = recipeDAO.getInputs(id, this.context.getSessionContext().getCurrentCollection().getId());
        List<ItemStackDTO> listaOutput = recipeDAO.getOutputs(id, this.context.getSessionContext().getCurrentCollection().getId());
        for (ItemStackDTO dto : listaInput) {
            createRecipeRow(
                    dto.item,
                    dto.amount,
                    input,
                    recipeDAO,
                    RecipeIOType.INPUT,
                    false
            );
        }

        for (ItemStackDTO dto : listaOutput) {
            createRecipeRow(
                    dto.item,
                    dto.amount,
                    output,
                    recipeDAO,
                    RecipeIOType.OUTPUT,
                    false
            );
        }
    }

    private void addInput(ItemDTO item, VBox target, RecipeDAO recipeDAO) {
        if (rowExists(target, item.name)) return;

        createRecipeRow(item, 1, target, recipeDAO, RecipeIOType.INPUT, true);
    }

    private void addOutput(ItemDTO item, VBox target, RecipeDAO recipeDAO) {
        if (rowExists(target, item.name)) return;

        createRecipeRow(item, 1, target, recipeDAO, RecipeIOType.OUTPUT, true);
    }

    public void deleteRecipe(EntryId id) {
        boolean delete = this.view.showAlert(
                "Eliminar receta",
                "Seguro que quieres eliminar esta receta?",
                Alert.AlertType.CONFIRMATION
        );

        if (delete) {
            RecipeDAO dao = (RecipeDAO) this.context.getDAO(DAOType.RECIPE);
            RecipeDTO dto = dao.read(id.value());
            dao.delete(id.value());
            this.context.getEntriesRepo().tryToRemoveEntry(id);
            this.view.showAlert("Receta eliminada", "La receta con nombre " + dto.name + " ha sido eliminada", Alert.AlertType.INFORMATION);
            Logger.getInstance().log(LogLevel.INFO, this.getClass().toString(), "El usuario " + this.context.getAccount().getUsername() + " ha borrado la receta con nombre " + dto.name + " en la coleccion " + this.context.getSessionContext().getCurrentCollection().getName());
            goBack();
        }
    }

    public void showRecipe(Integer id) {

        RecipeDAO dao = (RecipeDAO) this.context.getDAO(DAOType.RECIPE);
        RecipeDTO dto = dao.read(id);
        this.context.getEntriesRepo().addRecipe(this.context.getEntriesFactory().createRecipe(dto));
        Image image = ImageUtils.getImage(dto.iconPath);

        this.view.getNameField().setText(dto.name);
        this.view.getDescriptionArea().setText(dto.description);
        this.view.getEntryIcon().setImage(image);

        fillItems(id);

        this.buffer.publish(new ChangeScreenCommand(ViewType.SHOW_COLLECTION));
    }


    protected void goBack() {
        this.buffer.publish(new RedirectCommand(
                this.context.getSystemContext().getController(ViewType.SHOW_COLLECTION).getBuffer(),
                new ShowCollection(this.context.getSessionContext().getCurrentCollection())
        ));
    }

    private void createRecipeRow(
            ItemDTO item,
            int amountValue,
            VBox target,
            RecipeDAO recipeDAO,
            RecipeIOType type,
            boolean persist
    ) {
        TextField amount = UIPrefabsFactory.createAmountField();
        amount.setText(String.valueOf(amountValue));

        amount.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d+")) return;
            int newAmount = Integer.parseInt(newVal);
            if (newAmount <= 0) {
                this.view.showAlert("Cantidad incorrecta", "No puede haber un input/output con cantidad igual a 0", Alert.AlertType.WARNING);
            } else {
                RecipeDTO dto = this.context.getRecipeDTOById(this.view.getParentId().value());
                if (type == RecipeIOType.INPUT) {
                    recipeDAO.updateInputAmount(view.getParentId().value(), item.id, newAmount, this.context.getSessionContext().getCurrentCollection().getId());
                    Logger.getInstance().log(LogLevel.INFO, this.getClass().toString(),
                            "El usuario " + this.context.getAccount().getUsername() + " ha cambiado la cantidad a " + newAmount + " del input " + item.name + " en la receta " + dto.name + " en la coleccion " + this.context.getSessionContext().getCurrentCollection().getName());
                } else {
                    recipeDAO.updateOutputAmount(view.getParentId().value(), item.id, newAmount, this.context.getSessionContext().getCurrentCollection().getId());
                    Logger.getInstance().log(LogLevel.INFO, this.getClass().toString(),
                            "El usuario " + this.context.getAccount().getUsername() + " ha cambiado la cantidad a " + newAmount + " del output " + item.name + " en la receta " + dto.name + " en la coleccion " + this.context.getSessionContext().getCurrentCollection().getName());
                }
                }

        });

        Button delete = UIPrefabsFactory.createRemoveButton();
        Image icon = ImageUtils.getImage(item.iconPath);

        HBox row = UIPrefabsFactory.createRow(item.name, icon, delete, amount);
        target.getChildren().add(row);

        if (persist) {
            RecipeDTO dto = this.context.getRecipeDTOById(this.view.getParentId().value());
            if (type == RecipeIOType.INPUT) {
                Logger.getInstance().log(LogLevel.INFO, this.getClass().toString(),
                        "El usuario " + this.context.getAccount().getUsername() + " ha creado el input " + item.name + " en la receta " + dto.name + " en la coleccion " + this.context.getSessionContext().getCurrentCollection().getName());
                recipeDAO.insertSingleInput(view.getParentId().value(), item.id, amountValue, this.context.getSessionContext().getCurrentCollection().getId());
            } else {
                Logger.getInstance().log(LogLevel.INFO, this.getClass().toString(),
                        "El usuario " + this.context.getAccount().getUsername() + " ha creado el output " + item.name + " en la receta " + dto.name + " en la coleccion " + this.context.getSessionContext().getCurrentCollection().getName());
                recipeDAO.insertSingleOutput(view.getParentId().value(), item.id, amountValue, this.context.getSessionContext().getCurrentCollection().getId());
            }
        }

        delete.setOnAction(e -> {
            if (target.getChildren().size() <= 1) {
                this.view.showAlert("Item no eliminado",  "Una receta debe tener al menos un " + (type == RecipeIOType.INPUT ? "ingrediente" : "resultado"), Alert.AlertType.WARNING);
            } else {
                RecipeDTO dto = this.context.getRecipeDTOById(this.view.getParentId().value());
                target.getChildren().remove(row);
                if (type == RecipeIOType.INPUT) {
                    Logger.getInstance().log(LogLevel.INFO, this.getClass().toString(),
                            "El usuario " + this.context.getAccount().getUsername() + " ha eliminado el input " + item.name + " en la receta " + dto.name + " en la coleccion " + this.context.getSessionContext().getCurrentCollection().getName());
                    recipeDAO.deleteSingleInput(view.getParentId().value(), item.id, this.context.getSessionContext().getCurrentCollection().getId());
                } else {
                    Logger.getInstance().log(LogLevel.INFO, this.getClass().toString(),
                            "El usuario " + this.context.getAccount().getUsername() + " ha eliminado el output " + item.name + " en la receta " + dto.name + " en la coleccion " + this.context.getSessionContext().getCurrentCollection().getName());
                    recipeDAO.deleteSingleOutput(view.getParentId().value(), item.id, this.context.getSessionContext().getCurrentCollection().getId());
                }
            }
        });
    }

}