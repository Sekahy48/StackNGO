package mvc.model.inventory;

import java.util.ArrayList;
import java.util.List;

import mvc.model.entries.Item;

/**
 * ItemObject representa, en el contexto de inventarios, a todo nodo que represente un item final, un contenido sin capacidad de contener.
 * Ejemplos: unas monedas, un pan, una daga, 2 obillos de lana, etc
 */
public class ItemObject implements IInventoryElement {
    private int amount; 
    private Item item;

    public ItemObject(Item item, int amount) {
        if (amount < 0) {
            this.amount = 0;
        }else this.amount = amount;
        this.item = item;
    }

    //#region Utilidades basicas de inventario en global

    @Override
    public IInventoryElement addItem(Item item, int amount, boolean isNotLeaf) { 
        if (item == null){
        }else if(amount < 0){
        }
        else if (!isNotLeaf && this.item.equals(item)){
            this.amount += amount;
            return this;
        }else{
        }
        return null;
    }

    @Override 
    public void addSeveralLeafItems(List<ItemStack> items){
        for (ItemStack elem : items) {
            this.addItem(elem.getItem(), elem.getAmount(), false);
        }
    }

    @Override
    public int modifyAmount(Item item, int amount) {
        int out = amount;
        if(amount != 0 && this.item.equals(item)){
            this.amount += amount;
            if (this.amount < 0){
                out -= this.amount;
                this.amount = 0;
            } 
        } else {
        }
        return out;
    }

    @Override
    public boolean contains(Item item) {
        boolean out = false;
        if(this.find(item) != null){
            out = true;
        }
        return out;
        
    }

    @Override
    public int getAmount(Item item) {
        int out = 0;
        if (this.item.equals(item)) {
            out = this.amount;
        }
        return out;
    }
    
    @Override
    public void deleteItem(Item item) {
        if (this.item.equals(item)){
            this.amount = 0;
        }
    }

    @Override
    public IInventoryElement find(Item item) {
        if(this.item.equals(item)) return this;
        return null;
    }
    
    @Override
    public List<IInventoryElement> findNodes(Item item) {
        return null;
    }

    //#endregion
    //#region Utilidades basicas de inventario en local (afecta solo al inventario de este nodo del arbol)
    @Override
    public IInventoryElement addItemHere(Item item, int amount, boolean isNotLeaf) {
        return this.addItem(item, amount, isNotLeaf);
    }

    @Override 
    public void addSeveralLeafItemsHere(List<ItemStack> items){
        for (ItemStack elem : items) {
            this.addItemHere(elem.getItem(), elem.getAmount(), false);
        }
    }

    @Override
    public int modifyAmountHere(Item item, int amount) {
        return this.modifyAmount(item, amount);
    }

    @Override
    public boolean containsHere(Item item) {
        return this.contains(item);
    }

    @Override
    public int getAmountHere(Item item) {
        return this.getAmount(item);
    }

    @Override
    public void deleteItemHere(Item item) {
        this.deleteItem(item);
    }

    @Override
    public IInventoryElement findHere(Item item) {
        return this.find(item);
    }
    
    @Override
    public List<IInventoryElement> findNodesHere(Item item){
        return this.findNodes(item);
    }

    //#endregion
    //#region Getters, Setters & Utilities
    @Override
    public Item getItem() {
        return this.item;
    }

    @Override
    public List<IInventoryElement> getInventory(){
        return null;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    @Override
    public int getAmount() {
        return this.amount;
    }

    @Override
    public void cleanTree() {
    }

   


    //#endregion
    
    @Override
    public String toString(){
        return "\u001B[32m" + this.item.toString() + ". Amount " + this.amount + "\u001B[0m";
    }

    @Override
    public List<IInventoryElement> flattenInventory() {
        throw new UnsupportedOperationException("Unimplemented method 'flattenInventory'");
    }

    @Override
    public void clearInventory() {
        throw new UnsupportedOperationException("Unimplemented method 'clearInventory'");
    }
}
