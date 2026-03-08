package mvc.model.inventory;

import java.util.List;

import mvc.model.entries.Item;

/**
 * IInventoryElement represents, in the inventory context, any object that acts as a node
 * within an inventory tree, either as a sub-inventory or as a final content / leaf.
 */
public interface IInventoryElement {

    //#region Global inventory basic utilities

    /**
     * Adds a positive amount of an item to the current inventory.
     * In order to add it, it is necessary to specify whether the item
     * can contain other items or not.
     * The created or modified node in the inventory tree is returned.
     * If multiple inventory nodes are added at once, the last added node
     * is returned.
     *
     * @implNote Whether the item is a leaf or not will be managed in future
     * versions by item components.
     *
     * @param item       Item to add
     * @param amount     Positive amount to add
     * @param isNotLeaf  Indicates whether the added item can contain other items
     * @return The added or modified inventory node
     */
    public IInventoryElement addItem(Item item, int amount, boolean isNotLeaf);

    /**
     * Adds multiple leaf items with their specified amounts.
     *
     * @param items List of item stacks to add
     */
    public void addSeveralLeafItems(List<ItemStack> items);

    /**
     * Modifies the amount of an item in the current inventory by a non-zero value.
     *
     * @param item   Item to modify
     * @param amount Amount to add or remove
     * @return The actual amount modified
     */
    public int modifyAmount(Item item, int amount);

    /**
     * Checks whether the current inventory contains the given item.
     *
     * @param item Item to check
     * @return True if the item is present
     */
    public boolean contains(Item item);

    /**
     * Gets the total amount of the given item across the entire inventory,
     * including all nodes that contain it.
     *
     * @param item Item to query
     * @return Total amount of the item in the inventory
     */
    public int getAmount(Item item);

    /**
     * Removes any amount of the given item from the inventory.
     *
     * @param item Item to remove
     */
    public void deleteItem(Item item);

    /**
     * Returns the first node that contains the given item.
     *
     * @param item Item to search for
     * @return The first node containing the item, or null if not found
     */
    public IInventoryElement find(Item item);

    /**
     * Returns a list containing all nodes that contain the given item.
     *
     * @param item Item to search for
     * @return List of nodes containing the item
     */
    public List<IInventoryElement> findNodes(Item item);

    //#endregion

    //#region Local inventory basic utilities
    // (affect only the immediate inventory of this tree node)

    /**
     * Local variant of {@link #addItem(Item, int, boolean)}.
     * <p>
     * Adds the item only among the direct children of the inventory
     * from which the method is invoked, without recursively descending
     * into sub-inventories.
     *
     * @param item      Item to add
     * @param amount    Amount to add (positive)
     * @param isNotLeaf Indicates whether the added item can contain other items
     */
    public IInventoryElement addItemHere(Item item, int amount, boolean isNotLeaf);

    /**
     * Local variant of {@link #addSeveralLeafItems(List<ItemStack> items)}.
     * <p>
     * Adds leaf items only among the direct children of the inventory
     * from which the method is invoked, without descending recursively
     * into sub-inventories.
     *
     * @param items List of item stacks to add
     */
    public void addSeveralLeafItemsHere(List<ItemStack> items);

    /**
     * Local variant of {@link #modifyAmount(Item, int)}.
     * <p>
     * Modifies the item amount only in the direct child nodes
     * of the current inventory, without propagating to lower levels.
     *
     * @param item   Item to modify
     * @param amount Amount to add or remove
     * @return Amount actually modified at this level
     */
    public int modifyAmountHere(Item item, int amount);

    /**
     * Local variant of {@link #contains(Item)}.
     * <p>
     * Checks whether the current inventory contains the item
     * only among its direct children.
     *
     * @param item Item to check
     * @return True if the item is present at this level
     */
    public boolean containsHere(Item item);

    /**
     * Local variant of {@link #getAmount(Item)}.
     * <p>
     * Gets the amount of the item existing only among the
     * direct children of the current inventory.
     *
     * @param item Item to query
     * @return Amount of the item at this inventory level
     */
    public int getAmountHere(Item item);

    /**
     * Local variant of {@link #deleteItem(Item)}.
     * <p>
     * Removes any amount of the item only among the direct
     * children of the current inventory, without affecting sub-inventories.
     *
     * @param item Item to remove
     */
    public void deleteItemHere(Item item);

    /**
     * Local variant of {@link #find(Item)}.
     * <p>
     * Returns the first direct child node that contains the item,
     * without recursively descending into the tree.
     *
     * @param item Item to search for
     * @return Found node or null if it does not exist at this level
     */
    public IInventoryElement findHere(Item item);

    /**
     * Local variant of {@link #findNodes(Item)}.
     * <p>
     * Returns all direct child nodes that contain the item,
     * excluding results from lower levels of the tree.
     *
     * @param item Item to search for
     * @return List of nodes found at this level
     */
    public List<IInventoryElement> findNodesHere(Item item);

    //#endregion

    //#region Getters, Setters & Utilities

    /**
     * Removes all contents from the inventory.
     */
    public void clearInventory();

    /**
     * Returns the internal item.
     *
     * @return The item
     */
    public Item getItem();

    /**
     * Returns the internal inventory, if present.
     *
     * @return The internal inventory, if present
     */
    public List<IInventoryElement> getInventory();

    /**
     * Returns true if this node is a leaf / item without content.
     *
     * @return True if this node is a leaf
     */
    public boolean isLeaf();

    /**
     * Sets the amount of the internal item.
     *
     * @param amount New amount
     */
    public void setAmount(int amount);

    /**
     * Returns the amount of the internal item.
     *
     * @return Internal item amount
     */
    public int getAmount();

    /**
     * Traverses the inventory tree searching for nodes with amount == 0
     * and removes them from the inventory, cleaning the tree.
     */
    public void cleanTree();

    /**
     * Returns a linear list containing all items stored in this inventory
     * and all its sub-inventories, in order of appearance.
     * Inventory nodes are omitted; only real items (ItemObject) are included.
     *
     * @return Flattened list of inventory items
     */
    List<IInventoryElement> flattenInventory();

    //#endregion
}
