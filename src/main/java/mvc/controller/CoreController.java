package mvc.controller;
  
import java.util.HashMap;
import java.util.Map;

import event.EventBus;
import event.NavigateEvent;
import mvc.context.DataContext; 
import mvc.context.SessionContext;
import mvc.context.SystemContext;
import mvc.controller.add.AddCollectionController;
import mvc.controller.add.AddItemController;
import mvc.controller.add.AddRecipeController;
import mvc.controller.inventory.InventoryController;
import mvc.controller.modify.CollectionModifyController;
import mvc.controller.modify.ItemModifyController;
import mvc.controller.modify.RecipeModifyController;
import mvc.controller.show.multiple.ShowAccountsController;
import mvc.controller.show.multiple.ShowCollectionsController;
import mvc.controller.show.multiple.ShowComponentsController;
import mvc.controller.show.multiple.ShowItemsController;
import mvc.controller.show.single.ShowCollectionDataController;
import mvc.controller.show.single.ShowItemDataController;
import mvc.controller.show.single.ShowRecipeDataController;
import mvc.controller.user.LoginController;
import mvc.controller.user.PrivateController;
import mvc.controller.user.SignUpController;
import mvc.view.AbstractView;
import mvc.view.ScreenManager;
import mvc.view.ViewType;
import service.AccountService;
import service.CollectionService;
import service.ComponentService;
import service.InventoryService;
import service.ItemService;
import service.RecipeService;
import service.ServiceConsumer;
import service.ServiceType;
import service.SessionService;
import service.SystemService;

public class CoreController extends ServiceConsumer{

    private final DataContext dataContext;
    private final SessionContext sessionContext;
    private final SystemContext systemContext;

    private final Map<ViewType, AbstractController<?>> controllers = new HashMap<>();

    public CoreController(ScreenManager screenManager) {
        dataContext = new DataContext();
        sessionContext = new SessionContext();
        systemContext = new SystemContext(screenManager, this);
    }

    public void initControllers() {

        // ───── ADD ─────
        registerController(ViewType.ADD_COLLECTION, new AddCollectionController());

        registerController(ViewType.ADD_ITEM, new AddItemController());

        registerController(ViewType.ADD_RECIPE, new AddRecipeController());

        // ───── SHOW ─────
        registerController(ViewType.SHOW_COLLECTIONS, new ShowCollectionsController());

        registerController(ViewType.SHOW_COLLECTION, new ShowCollectionDataController());

        registerController(ViewType.SHOW_RECIPE, new ShowRecipeDataController());

        registerController(ViewType.SHOW_ITEM, new ShowItemDataController());

        registerController(ViewType.SHOW_ITEMS, new ShowItemsController());

        registerController(ViewType.SHOW_ACCOUNTS, new ShowAccountsController());

        // OJO: este antes NO hacía attachView
        /*ShowRecipesController showRecipes =
                new ShowRecipesController(new EventBuffer());
        showRecipes.setRuntimeContext(runtimeContext);
        controllers.put(ViewType.SHOW_RECIPES, showRecipes);*/

        // ───── MODIFY ─────
        registerController(ViewType.MODIFY_COLLECTION, new CollectionModifyController());

        registerController(ViewType.MODIFY_RECIPE, new RecipeModifyController());

        registerController(ViewType.MODIFY_ITEM, new ItemModifyController());

        // ───── USER ─────
        registerController(ViewType.LOG_IN, new LoginController());

        registerController(ViewType.SIGN_UP, new SignUpController());

        registerController(ViewType.MAIN, new MainViewController());

        registerController(ViewType.PRIVATE_ZONE, new PrivateController());

        // ───── INVENTORY ─────
        registerController(ViewType.INVENTORY, new InventoryController());

        // ───── COMPONENT ─────
        registerController(ViewType.SHOW_COMPONENTS, new ShowComponentsController());
        //registerController(ViewType.SHOW_COMPONENT, new ShowComponent);

        EventBus.getInstance().subscribe(NavigateEvent.class, this::onNavigateEvent);
        
    }

    private void onNavigateEvent(NavigateEvent event) {
        SystemService systemService = this.getService(ServiceType.SYSTEM);
        systemService.show(event.type());
    }

    private <T extends AbstractView> void registerController(ViewType viewType, AbstractController<T> controller) { 

        T view = (T) this.systemContext.getView(viewType);
         
        controllers.put(viewType, controller);


        for (ServiceType t : controller.requiredServices()) {
            controller.addService(this.getService(t));
        }
        
        controller.attachView(view);;
    }


    public <T extends AbstractView> AbstractController<T> getController(ViewType controller) {
        return (AbstractController<T>) controllers.get(controller);
    }

 
    
    public void initServices() {
        this.addService(new AccountService(dataContext));
        this.addService(new CollectionService(dataContext));
        this.addService(new ItemService(dataContext));
        this.addService(new RecipeService(dataContext));
        this.addService(new InventoryService(sessionContext));
        this.addService(new SessionService(sessionContext));
        this.addService(new SystemService(systemContext));
        this.addService(new ComponentService(dataContext));
    }
    
     








}
