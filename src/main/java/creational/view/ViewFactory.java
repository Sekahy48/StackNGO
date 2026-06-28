package creational.view;

import mvc.view.AbstractView;
import mvc.view.MainView;
import mvc.view.ViewType;
import mvc.view.add.AddCollectionView;
import mvc.view.add.AddItemView;
import mvc.view.add.AddRecipeView;
import mvc.view.inventory.InventoryView;
import mvc.view.modify.CollectionModifyView;
import mvc.view.modify.ItemModifyView;
import mvc.view.modify.RecipeModifyView;
import mvc.view.show.multiple.ShowAccountsView;
import mvc.view.show.multiple.ShowCollectionsView;
import mvc.view.show.multiple.ShowComponentsView;
import mvc.view.show.multiple.ShowItemsView;
import mvc.view.show.single.ShowCollectionDataView;
import mvc.view.show.single.ShowItemDataView;
import mvc.view.show.single.ShowRecipeDataView;
import mvc.view.user.LoginView;
import mvc.view.user.SignUpView;
import mvc.view.user.PrivateView; 

public class ViewFactory implements AbstractViewFactory {

    public ViewFactory() {
        super();
    }

    @Override
    public AbstractView create(ViewType view) {
        return switch (view) {
            case LOG_IN -> new LoginView();
            case SIGN_UP -> new SignUpView();
            case MAIN -> new MainView();
            case ADD_COLLECTION -> new AddCollectionView();
            case ADD_ITEM -> new AddItemView();
            case ADD_RECIPE -> new AddRecipeView();
            case PRIVATE_ZONE -> new PrivateView();
            case SHOW_COLLECTION -> new ShowCollectionDataView();
            case SHOW_COLLECTIONS ->  new ShowCollectionsView();
            case SHOW_RECIPE -> new ShowRecipeDataView();
            case SHOW_ITEM -> new ShowItemDataView();
            case SHOW_ITEMS -> new ShowItemsView();
            case SHOW_COMPONENTS -> new ShowComponentsView();
            case SHOW_ACCOUNTS ->  new ShowAccountsView();
            case MODIFY_COLLECTION -> new CollectionModifyView();
            case MODIFY_RECIPE -> new RecipeModifyView();
            case MODIFY_ITEM -> new ItemModifyView();
            case INVENTORY -> new InventoryView();
            default -> throw new IllegalArgumentException("View not supported: " + view);
        };
    }
}
