package mvc.model.inventory;

import mvc.model.entries.Item;

public class ItemStack implements Comparable<ItemStack>{
    private final Item item;
    private final int amount;

    public ItemStack(Item item, int amount) {
        this.item = item;
        this.amount = amount;
    }

    public Item getItem(){ 
        return item; 
    }
    public int getAmount(){ 
        return amount; 
    }

    @Override
    public int compareTo(ItemStack o) {
        return o.getItem().compareTo(this.item);
    }
}

