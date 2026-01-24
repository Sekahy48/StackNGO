package mvc.controller;
  
import java.util.HashMap;
import java.util.Map;

import dataTransportLayer.EventBuffer;
import mvc.context.RuntimeContext;
import mvc.controller.add.AddCollectionController;
import mvc.controller.add.AddItemController;
import mvc.controller.add.AddRecipeController;
import mvc.controller.inventory.InventoryController;
import mvc.controller.modify.CollectionModifyController;
import mvc.controller.modify.ItemModifyController;
import mvc.controller.modify.RecipeModifyController;
import mvc.controller.show.ShowAccountsController;
import mvc.controller.show.ShowCollectionsController;
import mvc.controller.show.ShowItemsController;
import mvc.controller.show.entry.data.ShowCollectionDataController;
import mvc.controller.show.entry.data.ShowItemDataController;
import mvc.controller.show.entry.data.ShowRecipeDataController;
import mvc.controller.user.LoginController;
import mvc.controller.user.PrivateController;
import mvc.controller.user.SignUpController;
import mvc.view.AbstractView;
import mvc.view.ViewType;

public class CoreController {

    private RuntimeContext context;

    private final Map<ViewType, AbstractController<?>> controllers = new HashMap<>();


    public void initControllers(RuntimeContext context){
        this.context = context;
        this.initControllers();
    }

    private void initControllers() {

    // ───── ADD ─────
    registerController(ViewType.ADD_COLLECTION, new AddCollectionController(new EventBuffer()));

    registerController(ViewType.ADD_ITEM, new AddItemController(new EventBuffer()));

    registerController(ViewType.ADD_RECIPE, new AddRecipeController(new EventBuffer()));

    // ───── SHOW ─────
    registerController(ViewType.SHOW_COLLECTIONS, new ShowCollectionsController(new EventBuffer()));

    registerController(ViewType.SHOW_COLLECTION, new ShowCollectionDataController(new EventBuffer()));

    registerController(ViewType.SHOW_RECIPE, new ShowRecipeDataController(new EventBuffer()));

    registerController(ViewType.SHOW_ITEM, new ShowItemDataController(new EventBuffer()));

    registerController(ViewType.SHOW_ITEMS, new ShowItemsController(new EventBuffer()));

    registerController(ViewType.SHOW_ACCOUNTS, new ShowAccountsController(new EventBuffer()));

    // OJO: este antes NO hacía attachView
    /*ShowRecipesController showRecipes =
            new ShowRecipesController(new EventBuffer());
    showRecipes.setRuntimeContext(runtimeContext);
    controllers.put(ViewType.SHOW_RECIPES, showRecipes);*/

    // ───── MODIFY ─────
    registerController(ViewType.MODIFY_COLLECTION, new CollectionModifyController(new EventBuffer()));

    registerController(ViewType.MODIFY_RECIPE, new RecipeModifyController(new EventBuffer()));

    registerController(ViewType.MODIFY_ITEM, new ItemModifyController(new EventBuffer()));

    // ───── USER ─────
    registerController(ViewType.LOG_IN, new LoginController(new EventBuffer()));

    registerController(ViewType.SIGN_UP, new SignUpController(new EventBuffer()));

    registerController(ViewType.MAIN, new MainViewController(new EventBuffer()));

    registerController(ViewType.PRIVATE_ZONE, new PrivateController(new EventBuffer()));

    // ───── INVENTORY ─────
    registerController(ViewType.INVENTORY, new InventoryController(new EventBuffer()));
}


    private <T extends AbstractView> void registerController(ViewType viewType, AbstractController<T> controller) {
        controller.setRuntimeContext(context);

        T view = (T) context.getSystemContext().getView(viewType);
        controller.attachView(view);

        controllers.put(viewType, controller);
    }


    public <T extends AbstractView> AbstractController<T> getController(ViewType controller) {
        return (AbstractController<T>) controllers.get(controller);
    }


    public void setContext(RuntimeContext context){
        this.context = context;
    }

    
     








}
