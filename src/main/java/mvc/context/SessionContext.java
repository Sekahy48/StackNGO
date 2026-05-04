package mvc.context;

import java.util.Deque;

import dataTransportLayer.CollectionDTO;
import dataTransportLayer.RecipeDTO;
import domain.accounts.Account;
import mvc.model.inventory.IInventoryElement;

public class SessionContext implements Context{
    private Account currentAccount;
    private CollectionDTO currentCollection;

    private CollectionDTO currentInventoryCollection;
    private RecipeDTO currentInventoryRecipe;
    private Deque<IInventoryElement> inventoryStack;
    
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
}
