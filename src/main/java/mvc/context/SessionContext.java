package mvc.context;

import dataTransportLayer.CollectionDTO;
import dataTransportLayer.RecipeDTO;
import domain.accounts.Account;

public class SessionContext implements Context{
    private Account currentAccount;
    private CollectionDTO currentCollection;

    private CollectionDTO currentInventoryCollection;
    private RecipeDTO currentInventoryRecipe;

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
}
