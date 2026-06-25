package service;
  

import dataTransportLayer.CollectionDTO;
import dataTransportLayer.ItemDTO;
import dataTransportLayer.RecipeDTO;
import domain.accounts.Account;
import mvc.context.SessionContext;

public class SessionService implements IService{

    private final SessionContext context;

    public SessionService(SessionContext context) {
        this.context = context;
    }

    @Override
    public ServiceType getType() {
        return ServiceType.SESSION;
    }
 
    public CollectionDTO getCurrentCollectionDTO() {
        return this.context.getCurrentCollection();
    }

    public Account getCurrentAccount() {
        return this.context.getCurrentAccount();
    }

    public CollectionDTO getCurrentInventoryCollectionDTO() {
        return this.context.getCurrentInventoryCollection();
    }

    public RecipeDTO getCurrentInventoryRecipeDTO() {
        return this.context.getCurrentInventoryRecipe();
    }

    public ItemDTO getCurrentItem() {
        return this.context.getCurrentItem();
    }


    public RecipeDTO getCurrentRecipeDTO() {
        return this.context.getCurrentRecipeDTO();
    }



    public void setCurrentCollection(CollectionDTO collection) {
        this.context.setCurrentCollection(collection);
    }

    public void setCurrentAccount(Account account) {
        this.context.setCurrentAccount(account);
    }

    public void untrackCurrentAccount(){
        this.setCurrentAccount(null);
    }

    public void setCurrentInventoryCollection(CollectionDTO collection) {
        this.context.setCurrentInventoryCollection(collection);
    }

    public void setCurrentInventoryRecipe(RecipeDTO recipe) {
        this.context.setCurrentInventoryRecipe(recipe);
    }

    public void setCurrentRecipe(RecipeDTO currentRecipeDTO) {
        this.context.setCurrentRecipeDTO(currentRecipeDTO);
    }

    public void setCurrentItem(ItemDTO currentItem) {
        this.context.setCurrentItem(currentItem);
    } 

    
}
