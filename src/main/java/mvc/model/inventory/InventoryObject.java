package mvc.model.inventory;

import java.util.ArrayList;
import java.util.List;

import mvc.model.entries.Item;

/**
 * InventoryObject representa, en el contexto de inventarios, a cualquier nodo que si o si contenga un inventario interno o la capacidad de 
 * albergarlo además de contener un item identitario
 * Ejemplos: una mochila, los bolsillos de una mochila, una bolsa, un cofre, o el propio inventario de un jugador o el inventario raiz (caso en 
 * el cual el atributo item es nulo, indicando que es la raiz de una jerarquia de inventario concreta)
 */
public class InventoryObject implements IInventoryElement {

    private Item item;
    private ArrayList<IInventoryElement> inventory; 
    private int amount = 1;

    public InventoryObject(Item item) {
        this.item = item;
        this.inventory = new ArrayList<>(); 
    }

    //#region Utilidades basicas de inventario en global
    @Override
    public IInventoryElement addItem(Item item, int amount, boolean isNotLeaf) {
        IInventoryElement out = null;
        if(amount <= 0){
        }else if(item == null){
        }else{
            if(isNotLeaf){
                out = new InventoryObject(item);
                for (int i = 0; i < amount; i++){
                    this.inventory.add(out);
                }
            }else{
                out = this.find(item);
                if (out != null){
                    out.modifyAmount(item, amount);
                }else{
                    out = new ItemObject(item, amount);
                    this.inventory.add(out);
                }
            }
        }
        return out;
    }

    @Override 
    public void addSeveralLeafItems(List<ItemStack> items){
        for (ItemStack elem : items) {
            this.addItem(elem.getItem(), elem.getAmount(), false);
        }
    }

    @Override
    public int modifyAmount(Item item, int amount) {
        int out = 0;

        List<IInventoryElement> toPromote = new ArrayList<>();
        List<IInventoryElement> toAdd = new ArrayList<>();

        for (int i = 0; i < inventory.size() && out != Math.abs(amount); i++) {
            IInventoryElement elem = inventory.get(i);
            boolean itemMatches = elem.getItem().equals(item);


            if (elem.isLeaf() && itemMatches) {
                out += elem.modifyAmount(item, amount - out);

            } else if (!elem.isLeaf() && itemMatches && amount < 0) {
                elem.setAmount(0);
                toPromote.addAll(elem.getInventory());
                out++;

            } else if (amount > 0 && !elem.isLeaf() && itemMatches) {
                for (int j = 0; j < amount; j++) toAdd.add(new InventoryObject(item));
                out += amount;

            } else if (!elem.isLeaf()) {
                out += elem.modifyAmount(item, amount - out);
            }
        }

        inventory.addAll(toAdd);
        if (!toPromote.isEmpty()) inventory.addAll(toPromote);
        if (amount < 0) cleanTree();

        if (out != Math.abs(amount) && !toPromote.isEmpty()) out += modifyAmount(item, amount - out);
        

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
        int count = 0;
        if(/*si no es root*/this.item != null && this.item.equals(item)) count++;
        for (IInventoryElement element : inventory) {
            count += element.getAmount(item);
            
        }
        return count;
    }

    @Override
    public void deleteItem(Item item) {
        this.modifyAmount(item, -this.getAmount(item));
    }

    @Override
    public IInventoryElement find(Item item) {
        IInventoryElement out = null;
        if(this.item != null && this.item.equals(item)){
            out = this;
        }else{
            for (int i = 0; i < this.inventory.size() && out == null; i++) {
                IInventoryElement element = this.inventory.get(i);
                out = element.find(item);
            }
        }
        
            return out;
    }

    @Override
    public List<IInventoryElement> findNodes(Item item) {
        List<IInventoryElement> nodes = new ArrayList<>();
        for (IInventoryElement element : inventory) {
            if (element.getItem().equals(item)) {
                nodes.add(element);
            }
            if (!element.isLeaf()) {
                nodes.addAll(element.findNodes(item));
            }
        }
        return nodes;
    }

    //#endregion
    
    //#region Utilidades basicas de inventario en local (afecta solo al inventario de este nodo del arbol)
    @Override
    public IInventoryElement addItemHere(Item item, int amount, boolean isNotLeaf) {
        IInventoryElement out = null;
        if(amount <= 0){
        }else if(item == null){
        }else{
            if(isNotLeaf){
                out = new InventoryObject(item);
                for (int i = 0; i < amount; i++){
                    this.inventory.add(out);
                }
            }else{
                out = this.findHere(item);
                if (out != null){
                    out.modifyAmountHere(item, amount);
                }else{
                    out = new ItemObject(item, amount);
                    this.inventory.add(out);
                }
            }
        }
        return out;
    }

    @Override 
    public void addSeveralLeafItemsHere(List<ItemStack> items){
        for (ItemStack elem : items) {
            this.addItemHere(elem.getItem(), elem.getAmount(), false);
        }
    }

@Override
    public int modifyAmountHere(Item item, int amount) {
        int modified = 0;

        // Listas auxiliares para añadir o promocionar
        List<IInventoryElement> toAdd = new ArrayList<>();
        List<IInventoryElement> toPromote = new ArrayList<>();

        // Recorremos el inventario inmediato
        for (int i = 0; i < inventory.size() && modified != Math.abs(amount); i++) {
            IInventoryElement elem = inventory.get(i);
            boolean matches = elem.getItem().equals(item);

            if (!matches) continue;

            if (elem.isLeaf()) {
                // Es hoja → solo modificar amount
                int delta = amount > 0 ? Math.min(amount - modified, Integer.MAX_VALUE)
                                    : -Math.min(Math.abs(amount) - modified, elem.getAmount());
                elem.modifyAmount(item, delta);
                modified += Math.abs(delta);

                // Si la hoja queda a 0 o menos, la eliminamos
                if (elem.getAmount() <= 0) {
                    inventory.remove(i);
                    i--;
                }

            } else {
                // Es nodo (InventoryObject)
                if (amount > 0) {
                    // Añadir n nodos
                    int toCreate = Math.min(amount - modified, Integer.MAX_VALUE);
                    for (int j = 0; j < toCreate; j++) {
                        toAdd.add(new InventoryObject(item));
                        modified++;
                    }
                } else {
                    // Eliminar n nodos
                    inventory.remove(i);
                    i--;
                    toPromote.addAll(elem.getInventory());
                    modified++;
                }
            }
        }

        // Añadimos los nuevos nodos
        inventory.addAll(toAdd);
        // Promocionamos contenido de nodos eliminados
        inventory.addAll(toPromote);

        // Limpiar nodos con amount <= 0
        if (amount < 0) cleanTree();

        return modified;
    }



    @Override
    public boolean containsHere(Item item) {
        boolean isHere = false;
        for (int i = 0; i < this.inventory.size() && !isHere; i++) 
            isHere = this.inventory.get(i).getItem().equals(item);
        return isHere;
    }

    @Override
    public int getAmountHere(Item item) {
        int out = 0;
        for (IInventoryElement element : inventory) {
            out += element.getItem().equals(item) ? element.getAmount() : 0;
        }
        return out;
    }

    @Override
    public void deleteItemHere(Item item) {
        int count = 0;
        for (IInventoryElement elem : inventory) {
            // Contamos nodos de tipo InventoryObject que tengan ese item
            if (!elem.isLeaf() && elem.getItem().equals(item)) {
                count++;
            }
            // Contamos hojas
            else if (elem.isLeaf() && elem.getItem().equals(item)) {
                count += elem.getAmount();
            }
        }
        if (count > 0) {
            this.modifyAmountHere(item, -count);
        }
    }


    

    @Override
    public IInventoryElement findHere(Item item) {
        for (IInventoryElement element : inventory) {
            if (element.getItem().equals(item)) return element;
        }
        return null;
    }

    @Override
    public List<IInventoryElement> findNodesHere(Item item) {
        List<IInventoryElement> out = new ArrayList<>();
        for (IInventoryElement element : inventory) {
            if (element.getItem().equals(item)) {
                out.add(element);
            }
        }
        return out;
    }

    @Override
    public List<IInventoryElement> flattenInventory() {
        List<IInventoryElement> flatList = new ArrayList<>();
        flattenHelper(this, flatList, true); // 'true' indica que es root y no se añade
        return flatList;
    }

    /**
     * @param skipRoot indica si este nodo es el root y no debe añadirse
     */
    private void flattenHelper(IInventoryElement node, List<IInventoryElement> accumulator, boolean skipRoot) {
        if (!node.isLeaf()) {
            // Solo añadimos nodos contenedores si no son root y tienen contenido
            if (!skipRoot && node.getInventory() != null && !node.getInventory().isEmpty()) {
                accumulator.add(node);
            }
            if (node.getInventory() != null) {
                for (IInventoryElement child : node.getInventory()) {
                    flattenHelper(child, accumulator, false);
                }
            }
        } else {
            // Nodo hoja → añadir directamente
            accumulator.add(node);
        }
    }


    //endregion
    
    //#region Getters, Setters & Utilities
    @Override
    public Item getItem() {
        return this.item;
    }

    @Override
    public List<IInventoryElement> getInventory() {
        return this.inventory;
    }

    @Override
    public boolean isLeaf() {
        return false;
    } 

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public int getAmount(){
        return this.amount;
    }

    @Override
    public void cleanTree() {
        for (IInventoryElement elem : inventory) {
            elem.cleanTree();
        }
        inventory.removeIf(e -> e.getAmount() <= 0);
    }


    //#endregion
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder(
            this.item != null ? "\u001B[36m" + this.item.toString() + ". Amount " + this.amount + "\u001B[0m" : "\u001B[36m" + "Root Inventory" + "\u001B[0m"
        );

        for (IInventoryElement elem : inventory) {
            String child = elem.toString();

            // Indentamos TODAS las líneas del hijo
            child = child.replace("\n", "\n\t");

            out.append("\n\t").append(child);
        }

        return out.toString();
    }

    @Override
    public void clearInventory() {
        this.inventory.clear();
    }


    

    
    


    
}
