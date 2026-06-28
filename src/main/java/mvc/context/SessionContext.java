package mvc.context;

import java.util.ArrayDeque;
import java.util.Deque;

import dataTransportLayer.CollectionDTO;
import dataTransportLayer.ComponentDefinitionDTO;
import dataTransportLayer.ItemDTO;
import dataTransportLayer.RecipeDTO;
import domain.accounts.Account;
import mvc.model.inventory.IInventoryElement;

public class SessionContext implements Context{
    private Account currentAccount;
    private CollectionDTO currentCollection;
    private ItemDTO currentItem;
    private RecipeDTO currentRecipe;
    private ComponentDefinitionDTO currentComponent;

    private CollectionDTO currentInventoryCollection;
    private RecipeDTO currentInventoryRecipe;
    private Deque<IInventoryElement> inventoryStack = new ArrayDeque<>();
    
    public Account getCurrentAccount() { 
        return currentAccount; 
    }

    public void setCurrentAccount(Account account) { 
        this.currentAccount = account; 
    }

    public CollectionDTO getCurrentCollection() { 
        return currentCollection; 
    }

    public void setCurrentCollection(CollectionDTO collection) { 
        this.currentCollection = collection; 
    }

    public CollectionDTO getCurrentInventoryCollection() { 
        return currentInventoryCollection; 
    }

    public void setCurrentInventoryCollection(CollectionDTO collection) { 
        this.currentInventoryCollection = collection; 
    }

    public RecipeDTO getCurrentInventoryRecipe() { 
        return currentInventoryRecipe; 
    }

    public void setCurrentInventoryRecipe(RecipeDTO recipe) { 
        this.currentInventoryRecipe = recipe; 
    }

    public Deque<IInventoryElement> getInventoryStack() {
        return inventoryStack;
    }

    public void resetCurrentInventory() {
        throw new UnsupportedOperationException("Metodo sin completar", null);
    }

    public ItemDTO getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(ItemDTO currentItem) {
        this.currentItem = currentItem;
    }

    public RecipeDTO getCurrentRecipeDTO() {
        return currentRecipe;
    }

    public void setCurrentRecipe(RecipeDTO currentRecipeDTO) {
        this.currentRecipe = currentRecipeDTO;
    }

    public ComponentDefinitionDTO getCurrentComponentDTO() {
        return this.currentComponent;
    }

    public void setCurrentComponent(ComponentDefinitionDTO currentComponentDTO) {
        this.currentComponent = currentComponentDTO;
    }
}
