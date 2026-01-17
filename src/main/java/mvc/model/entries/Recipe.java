package mvc.model.entries;

import java.util.ArrayList;
import java.util.Collections;

import logger.LogLevel;
import logger.Logger;
 
/**
 * Models a crafting recipe that transforms a set of input items into output items.
 */

public class Recipe extends Entry {

    private ArrayList<ItemIdStack> input = new ArrayList<>();
    private ArrayList<ItemIdStack> output = new ArrayList<>();

    /**
     * Creates a fully defined recipe with specified input and output items.
     *
     * @param name        recipe name
     * @param description human-readable description of the recipe
     * @param imagePath   path to the image representing the recipe
     * @param id          unique identifier of the recipe
     * @param input       list of required input items
     * @param output      list of produced output items
     *
     * @throws IllegalArgumentException if {@code input} or {@code output} is {@code null}
     */
    public Recipe(String name, String description, String imagePath, int id, ArrayList<ItemIdStack> input, ArrayList<ItemIdStack> output) {
        super(name, description, imagePath, id);
        if(input == null || output == null){
            throw new IllegalArgumentException("Input and Output lists cannot be null");
        }
        this.input = input;
        this.output = output;
        
    }
    
    /**
     * Creates an empty recipe with no input or output items defined.
     *
     * @param name        recipe name
     * @param id          unique identifier of the recipe
     * 
     * Input and output lists must be populated before the recipe can be applied.
     */
    public Recipe(String name, int id) {
        super(name, id);
    }
    
    /**
     * Creates an empty recipe with no input or output items defined.
     *
     * @param name        recipe name
     * @param description human-readable description of the recipe
     * @param imagePath   path to the image representing the recipe
     * @param id          unique identifier of the recipe
     * 
     * Input and output lists must be populated before the recipe can be applied.
     */
    public Recipe(String name, String description, String imagePath, int id){
        super(name, description, imagePath, id);
    }

    //#region Getters and Setters
    public ArrayList<ItemIdStack> getIngredients() {
        return input;
    }

    public ArrayList<ItemIdStack> getResults() {
        return output;
    }

    /**
     * Sets the ingredients list, if null throws IllegalArgumentException because input list is a necesary 
     * and essential atribute of a recipe.
     * @param input     
     */
    public void setIngredients(ArrayList<ItemIdStack> input) {
        if(input == null){
            throw new IllegalArgumentException("Input list cannot be null");
        }
        this.input = input;
        this.sortInAndOut();
    }

    /**
     * Sets the results list, if null throws IllegalArgumentException because output list is a necesary 
     * and essential atribute of a recipe.
     * @param input     
     */
    public void setResults(ArrayList<ItemIdStack> output) {
        if(output == null){
            throw new IllegalArgumentException("Output list cannot be null");
        }
        this.output = output;
        this.sortInAndOut();
    }

    //#endregion

    //#region Recipe Related Methods
    /**
     * This method checks if its possible execute a recipe given an external input list
     * @param realInput
     * @return true if realInput matches inner input requirements
     */
    public boolean canBeExecuted(ArrayList<ItemIdStack> realInput){
    if (realInput == null) { 
        Logger.getInstance().log(LogLevel.ERROR, this.getClass().toString() + " con id " + this.id.toString(),
         "El metodo canBeExecuted ha sido ejecutado con el parametro \"realInput\" como nulo.\n");
        return false;
    }

    for (ItemIdStack needed : this.input) {

        ItemIdStack found = realInput.stream()
                .filter(e -> e.getId().equals(needed.getId()))
                .findFirst()
                .orElse(null);

        if (found == null || found.getAmount() < needed.getAmount()) {
            return false;
        }
    }

    return true;
}

    
    /**
     * This method executes the recipe, validating that the input is sufficient and the 
     * returning the output list
     * @param realInput
     * @return the results of the recipe if the input is sufficient
     */
    public ArrayList<ItemIdStack> executeRecipe(ArrayList<ItemIdStack> realInput){
        ArrayList<ItemIdStack> out = null;
        if (this.canBeExecuted(realInput)){
            out = new ArrayList<>(this.output);
            Logger.getInstance().log(LogLevel.INFO, this.getClass() + " con id " + this.getId().toString(), "La receta se ha ejecutado correctamente.");
        }else{
            Logger.getInstance().log(LogLevel.INFO, this.getClass() + " con id " + this.getId().toString(), "La receta no ha sido ejecutada.");
        }
        
        return out;
    }

    /**
     * This method adds an ingredient/output to the recipe, it doesnt 
     * @param item
     * @param amount
     */
    public void addIngredient(Item item, int amount){
        if(item != null){
            this.input.add(new ItemIdStack(item.getId(), amount));
            this.sortInAndOut();
        }else{
            Logger.getInstance().log(LogLevel.ERROR, this.getClass().toString() + " con id " + this.id.toString(), "Se ha intentado usar un valor nulo para añadir un ingrediente. Abortando intento\n");
        }
    }

    public void addResult(Item item, int amount){
        if(item != null){
            this.output.add(new ItemIdStack(item.getId(), amount));
            this.sortInAndOut();
        }else{
            Logger.getInstance().log(LogLevel.ERROR, this.getClass().toString() + " con id " + this.id.toString(), "Se ha intentado usar un valor nulo para añadir un resultado. Abortando intento\n");
        }
    }

    public boolean removeIngredient(Item item) {
        for (ItemIdStack stack : input) {
            if (stack.getId().equals(item.getId())) {
                input.remove(stack);
                return true;
            }
        }
        return false;
    }


    public boolean removeResult(Item item){
        for (ItemIdStack stack : output) {
            if (stack.getId().equals(item.getId())) {
                output.remove(stack);
                return true;
            }
        }
        return false;
    }
    //#endregion

    private void sortInAndOut(){
        this.input.sort(null);
        this.output.sort(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Recipe other = (Recipe) o;
        return input.equals(other.input) &&
            output.equals(other.output);
    }

}
