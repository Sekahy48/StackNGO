package mvc.model.entries;


import identificators.EntryId;

public class ItemIdStack implements Comparable<ItemIdStack>{
    private final EntryId id;
    private final int amount;

    public ItemIdStack(int id, int amount) {
        this.id = new EntryId(id);
        this.amount = amount;
    }

    public ItemIdStack(EntryId id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    public ItemIdStack(Item item, int amount) {
        this.id = item.getId();
        this.amount = amount;
    }

    public EntryId getId(){ 
        return this.id; 
    }
    public int getAmount(){ 
        return amount; 
    }

    @Override
    public int compareTo(ItemIdStack o) {
        return o.getId().compareTo(this.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemIdStack)) return false;
        ItemIdStack other = (ItemIdStack) o;
        return this.amount == other.amount && this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return 31 * id.hashCode() + amount;
    }

}
