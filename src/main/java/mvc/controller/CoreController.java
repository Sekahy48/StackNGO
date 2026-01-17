package mvc.controller;
  
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
import mvc.controller.show.ShowRecipesController;
import mvc.controller.show.entry.data.ShowCollectionDataController;
import mvc.controller.show.entry.data.ShowItemDataController;
import mvc.controller.show.entry.data.ShowRecipeDataController;
import mvc.controller.user.LoginController;
import mvc.controller.user.PrivateController;
import mvc.controller.user.SignUpController;
import mvc.view.ViewType;

public class CoreController {

    private final RuntimeContext runtimeContext;

    //#region instanciado de controllers
    //add
    private AddCollectionController addCollectionController;
    private AddItemController addItemController;
    private AddRecipeController addRecipeController;

    //show
    private ShowCollectionsController showCollectionsController;
    private ShowCollectionDataController showCollectionDataController;
    private ShowRecipeDataController showRecipeDataController;
    private ShowItemDataController showItemDataController;
    private ShowAccountsController showAccountsController;
    private ShowItemsController showItemsController;
    private ShowRecipesController showRecipesController;

    //modify
    private CollectionModifyController modifyCollectionDataController;
    private RecipeModifyController modifyRecipeDataController;
    private ItemModifyController modifyItemDataController;

    //user
    private LoginController loginController;
    private SignUpController signUpController;
    private MainViewController mainViewController;
    private PrivateController privateController;

    //inventory
    private InventoryController inventoryController;


    //#endregion

    public CoreController(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
        this.runtimeContext.setCoreController(this);
        this.createController();
    }

    private void createController() {
        //add
        EventBuffer addCollectionBuffer = new EventBuffer();
        this.addCollectionController = new AddCollectionController(addCollectionBuffer);
        this.addCollectionController.setRuntimeContext(runtimeContext);
        this.addCollectionController.attachView(runtimeContext.getScreenManager().getView(ViewType.ADD_COLLECTION));

        EventBuffer addItemBuffer = new EventBuffer();
        this.addItemController = new AddItemController(addItemBuffer);
        this.addItemController.setRuntimeContext(runtimeContext);
        this.addItemController.attachView(runtimeContext.getScreenManager().getView(ViewType.ADD_ITEM));

        EventBuffer addRecipeBuffer = new EventBuffer();
        this.addRecipeController = new AddRecipeController(addRecipeBuffer);
        this.addRecipeController.setRuntimeContext(runtimeContext);
        this.addRecipeController.attachView(runtimeContext.getScreenManager().getView(ViewType.ADD_RECIPE));

        //show

        EventBuffer showCollectionsBuffer = new EventBuffer();
        this.showCollectionsController = new ShowCollectionsController(showCollectionsBuffer);
        this.showCollectionsController.setRuntimeContext(runtimeContext);
        this.showCollectionsController.attachView(runtimeContext.getScreenManager().getView(ViewType.SHOW_COLLECTIONS));

        EventBuffer showCollectionDataBuffer = new EventBuffer();
        this.showCollectionDataController = new ShowCollectionDataController(showCollectionDataBuffer);
        this.showCollectionDataController.setRuntimeContext(runtimeContext);
        this.showCollectionDataController.attachView(runtimeContext.getScreenManager().getView(ViewType.SHOW_COLLECTION));

        EventBuffer showRecipeDataBuffer = new EventBuffer();
        this.showRecipeDataController = new ShowRecipeDataController(showRecipeDataBuffer);
        this.showRecipeDataController.setRuntimeContext(runtimeContext);
        this.showRecipeDataController.attachView(runtimeContext.getScreenManager().getView(ViewType.SHOW_RECIPE));

        EventBuffer showItemDataBuffer = new EventBuffer();
        this.showItemDataController = new ShowItemDataController(showItemDataBuffer);
        this.showItemDataController.setRuntimeContext(runtimeContext);
        this.showItemDataController.attachView(runtimeContext.getScreenManager().getView(ViewType.SHOW_ITEM));

        EventBuffer showAccountsBuffer = new EventBuffer();
        this.showAccountsController = new ShowAccountsController(showAccountsBuffer);
        this.showAccountsController.setRuntimeContext(runtimeContext);
        this.showAccountsController.attachView(runtimeContext.getScreenManager().getView(ViewType.SHOW_ACCOUNTS));

        EventBuffer showItemsControllerBuffer = new EventBuffer();
        this.showItemsController = new ShowItemsController(showItemsControllerBuffer);
        this.showItemsController.setRuntimeContext(runtimeContext);

        EventBuffer showRecipesBuffer = new EventBuffer();
        this.showRecipesController = new ShowRecipesController(showRecipesBuffer);
        this.showRecipesController.setRuntimeContext(runtimeContext);

        //modify

        EventBuffer modifyCollectionBuffer = new EventBuffer();
        this.modifyCollectionDataController = new CollectionModifyController(modifyCollectionBuffer);
        this.modifyCollectionDataController.setRuntimeContext(runtimeContext);
        this.modifyCollectionDataController.attachView(runtimeContext.getScreenManager().getView(ViewType.MODIFY_COLLECTION));

        EventBuffer modifyRecipeBuffer = new EventBuffer();
        this.modifyRecipeDataController = new RecipeModifyController(modifyRecipeBuffer);
        this.modifyRecipeDataController.setRuntimeContext(runtimeContext);
        this.modifyRecipeDataController.attachView(runtimeContext.getScreenManager().getView(ViewType.MODIFY_RECIPE));

        EventBuffer modifyItemBuffer = new EventBuffer();
        this.modifyItemDataController = new ItemModifyController(modifyItemBuffer);
        this.modifyItemDataController.setRuntimeContext(runtimeContext);
        this.modifyItemDataController.attachView(runtimeContext.getScreenManager().getView(ViewType.MODIFY_ITEM));

        //user
        EventBuffer loginBuffer = new EventBuffer();
        this.loginController = new LoginController(loginBuffer);
        this.loginController.setRuntimeContext(runtimeContext);
        this.loginController.attachView(runtimeContext.getScreenManager().getView(ViewType.LOG_IN));

        EventBuffer signUpBuffer = new EventBuffer();
        this.signUpController = new SignUpController(signUpBuffer);
        this.signUpController.setRuntimeContext(runtimeContext);
        this.signUpController.attachView(runtimeContext.getScreenManager().getView(ViewType.SIGN_UP));

        EventBuffer mainViewBuffer = new EventBuffer();
        this.mainViewController = new MainViewController(mainViewBuffer);
        this.mainViewController.setRuntimeContext(runtimeContext);
        this.mainViewController.attachView(runtimeContext.getScreenManager().getView(ViewType.MAIN));

        EventBuffer privateBuffer = new EventBuffer();
        this.privateController = new PrivateController(privateBuffer);
        this.privateController.setRuntimeContext(runtimeContext);
        this.privateController.attachView(runtimeContext.getScreenManager().getView(ViewType.PRIVATE_ZONE));

        //inventory
        EventBuffer inventoryBuffer = new EventBuffer();
        this.inventoryController = new InventoryController(inventoryBuffer);
        this.inventoryController.setRuntimeContext(runtimeContext); 
        this.inventoryController.attachView(runtimeContext.getScreenManager().getView(ViewType.INVENTORY));

    }

    //#region getters de los buffers
    public EventBuffer getAddCollectionBuffer() {
        return this.addCollectionController.getBuffer();
    }
    public EventBuffer getAddItemBuffer() {
        return this.addItemController.getBuffer();
    }
    public EventBuffer getAddRecipeBuffer() {
        return this.addRecipeController.getBuffer();
    }
    public EventBuffer getShowCollectionsBuffer() {
        return this.showCollectionsController.getBuffer();
    }
    public EventBuffer getLoginBuffer() {
        return this.loginController.getBuffer();
    }
    public EventBuffer getSignUpBuffer() {
        return this.signUpController.getBuffer();
    }
    public EventBuffer getMainViewBuffer() {
        return this.mainViewController.getBuffer();
    }
    public EventBuffer getPrivateBuffer() {
        return this.privateController.getBuffer();
    }
    public EventBuffer getAddCollectionControllerBuffer() {
        return addCollectionController.getBuffer();
    }
    public EventBuffer getAddItemControllerBuffer() {
        return addItemController.getBuffer();
    }
    public EventBuffer getAddRecipeControllerBuffer() {
        return addRecipeController.getBuffer();
    }
    public EventBuffer getShowCollectionDataBuffer() { return this.showCollectionDataController.getBuffer(); }
    public EventBuffer getShowRecipeDataBuffer() { return this.showRecipeDataController.getBuffer(); }
    public EventBuffer getShowItemDataBuffer() { return this.showItemDataController.getBuffer(); }
    public EventBuffer getShowAccountsBuffer() { return this.showAccountsController.getBuffer(); }

    public EventBuffer getModifyCollectionBuffer() { return this.modifyCollectionDataController.getBuffer(); }
    public EventBuffer getModifyRecipeBuffer() { return this.modifyRecipeDataController.getBuffer(); }
    public EventBuffer getModifyItemBuffer() { return this.modifyItemDataController.getBuffer(); }

    public EventBuffer getInventoryItemBuffer() { return this.inventoryController.getBuffer(); }
    //#endregion

    public void setUserNameInPrivateZone() {
        this.privateController.setUserName();
    }








}
