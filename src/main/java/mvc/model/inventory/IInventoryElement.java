package mvc.model.inventory;

import java.util.List;

import mvc.model.entries.Item;

/**
 * IInventoryElement representa, en el contexto de inventarios, a cualquier objeto que represente un nodo dentro de un inventario
 * bien sea un subinventario o un contenido final / hoja
 */
public interface IInventoryElement {
    //#region Utilidades basicas de inventario en global

    /**
     * Añade al inventario actual amount cantidad positiva de item al inventario
     * De cara a añadirlo se necesita especificar si puede contener otros items o no
     * Se devuelve el nodo creado en el arbol o el modificado al añadir la cantidad del item, 
     * en caso de añadir varios nodos inventario de vez, se devuelve el ultimo añadido.
     * @implNote Si es hoja o no se gestionará en futuras versiones por componentes del item
     * @param item
     * @param amount
     * @param isNotLeaf
     * @return el nodo añadido
     */
    public IInventoryElement addItem(Item item, int amount, boolean isNotLeaf);

    /**
     * Añade varios items finales con sus amounts especificadas
     * @param items
    */
    public void addSeveralLeafItems(List<ItemStack> items);

    /**
     * Modifica la cantidad de item en el inventario actual una cantidad amount distinta de 0
     * @param item
     * @param amount
     * @return cantidad modificada 
     */
    public int modifyAmount(Item item, int amount);

    /**
     * Comprueba si el inventario actual contiene item
     * @param item
     * @return si contiene el item
     */
    public boolean contains(Item item);

    /**
     * Obtiene la cantidad de item que hay en todo el inventario entre los diferentes nodos que lo contengan
     * @param item
     * @return la cantidad de item a lo largo de todo el inventario
     */
    public int getAmount(Item item);

    /**
     * Elimina cualquier cantidad de item en el inventario
     * @param item
     */
    public void deleteItem(Item item);

    /**
     * Obtiene el primer nodo que contenga item
     * @param item
     * @return el primer nodo que contenga item en caso de encontrarse
     */
    public IInventoryElement find(Item item);

    /**
     * Devuelve una lista compuesta por todos los nodos encontrados que contengan item
     * @param item
     * @return la lista de nodos que contengan item
     */
    public List<IInventoryElement> findNodes(Item item);
    //#endregion

    //#region Utilidades basicas de inventario en local
    // (afectan únicamente al inventario inmediato de este nodo del árbol)

    /**
     * Variante local de {@link #addItem(Item, int, boolean)}.
     * <p>
     * Añade el item únicamente entre los hijos directos del inventario
     * desde el que se invoca el método, sin descender recursivamente
     * en subinventarios.
     *
     * @param item      Item a añadir
     * @param amount    Cantidad a añadir (positiva)
     * @param isNotLeaf Indica si el item añadido puede contener otros items
     */
    public IInventoryElement addItemHere(Item item, int amount, boolean isNotLeaf);

    /**
     * Variante local de {@link #addSeveralLeafItems(List<ItemStack> items)}.
     * <p>
     * Añade los items hoja únicamente entre los hijos directos del inventario
     * desde el que se invoca el método, sin descender recursivamente
     * en subinventarios.
     * @param items
    */
    public void addSeveralLeafItemsHere(List<ItemStack> items);
    
    /**
     * Variante local de {@link #modifyAmount(Item, int)}.
     * <p>
     * Modifica la cantidad del item solo en los nodos hijos directos
     * del inventario actual, sin propagarse a niveles inferiores.
     *
     * @param item   Item a modificar
     * @param amount Cantidad a añadir o eliminar
     * @return Cantidad realmente modificada en este nivel
     */
    public int modifyAmountHere(Item item, int amount);

    /**
     * Variante local de {@link #contains(Item)}.
     * <p>
     * Comprueba si el inventario actual contiene el item únicamente
     * entre sus hijos directos.
     *
     * @param item Item a comprobar
     * @return true si el item está presente en este nivel
     */
    public boolean containsHere(Item item);

    /**
     * Variante local de {@link #getAmount(Item)}.
     * <p>
     * Obtiene la cantidad del item existente únicamente en los hijos
     * directos del inventario actual.
     *
     * @param item Item a consultar
     * @return Cantidad del item en este nivel del inventario
     */
    public int getAmountHere(Item item);

    /**
     * Variante local de {@link #deleteItem(Item)}.
     * <p>
     * Elimina cualquier cantidad del item únicamente entre los hijos
     * directos del inventario actual, sin afectar a subinventarios.
     *
     * @param item Item a eliminar
     */
    public void deleteItemHere(Item item);

    /**
     * Variante local de {@link #find(Item)}.
     * <p>
     * Devuelve el primer nodo hijo directo que contenga el item,
     * sin descender recursivamente en el árbol.
     *
     * @param item Item a buscar
     * @return Nodo encontrado o null si no existe en este nivel
     */
    public IInventoryElement findHere(Item item);

    /**
     * Variante local de {@link #findNodes(Item)}.
     * <p>
     * Devuelve todos los nodos hijos directos que contengan el item,
     * sin incluir resultados de niveles inferiores del árbol.
     *
     * @param item Item a buscar
     * @return Lista de nodos encontrados en este nivel
     */
    public List<IInventoryElement> findNodesHere(Item item);

    //#endregion

    //#region Getters, Setters & Utilities

    public void clearInventory();
    
    /**
     * Devuelve el item interno
     * @return el item
     */
    public Item getItem();

    /**
     * Devuelve el inventario interno, si lo tiene
     * @return el inventario interno, si lo tiene
     */
    public List<IInventoryElement> getInventory();

    /**
     * Devuelve true si es hoja / item sin contenido
     * @return true si es hoja
     */
    public boolean isLeaf();

    /**
     * Establece la cantidad del item interno a amount
     * @param amount
     */
    public void setAmount(int amount);

    /**
     * Devuelve la cantidad del item interno
     * @return la cantidad del item interno
     */
    public int getAmount();

    /**
     * Recorre el arbol en busqueda de nodos con amount == 0 para eliminarlos
     * del inventario, impia el inventario.
     */
    public void cleanTree();

    /**
    * Devuelve una lista lineal con todos los items contenidos en este inventario
    * y en todos sus sub-inventarios, en orden de aparición.
    * Los nodos inventario se omiten, solo se incluyen los items reales (ItemObject)
    */
    List<IInventoryElement> flattenInventory(); 

    //#endregion
}