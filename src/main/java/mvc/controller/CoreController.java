package mvc.controller;
  
import java.util.HashMap;
import java.util.Map;

import event.EventBus;
import event.NavigateEvent;
import mvc.context.DataContext; 
import mvc.context.SessionContext;
import mvc.context.SystemContext;
import mvc.controller.add.*;
import mvc.controller.show.single.*;
import mvc.controller.show.multiple.*; 
import mvc.controller.modify.*;
import mvc.controller.user.*;
import mvc.utils.DataExporter;
import mvc.utils.DataImporter;
import mvc.controller.inventory.*;
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

    private DataImporter dataImporter;
    private DataExporter dataExporter;

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

        PrivateController privateController = new PrivateController();
        privateController.setImporter(this.dataImporter).setExporter(this.dataExporter); 
        registerController(ViewType.PRIVATE_ZONE, privateController); 

        // ───── INVENTORY ─────
        registerController(ViewType.INVENTORY, new InventoryController());

        // ───── COMPONENT ─────
        registerController(ViewType.SHOW_COMPONENTS, new ShowComponentsController());
        registerController(ViewType.ADD_COMPONENT, new AddComponentController());
        registerController(ViewType.SHOW_COMPONENT, new ShowComponentDataController());

        EventBus.getInstance().subscribe(NavigateEvent.class, this::onNavigateEvent);
        
    }

    private void onNavigateEvent(NavigateEvent event) {
        SystemService systemService = this.getService(ServiceType.SYSTEM);
        systemService.show(event.type());
    }

    private <T extends AbstractView> void registerController(ViewType viewType, AbstractController<T> controller) { 

        T view = (T) this.systemContext.getView(viewType);
         
        controllers.put(viewType, controller);


        this.suplyServices(controller);
        
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

    public void initUtilities() {
        
        this.dataImporter = new DataImporter();
        this.dataExporter = new DataExporter();
        this.suplyServices(this.dataImporter);
        this.suplyServices(this.dataExporter);
    }

    public void suplyServices(ServiceConsumer consumer) {
        for (ServiceType t : consumer.requiredServices()) {
            consumer.addService(this.getService(t));
        }
    }
    
     








}
