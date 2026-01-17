package mvc.controller.modify;

public enum ModType {
    ITEM("El item"),
    RECIPE("La receta"),
    COLLECTION("La coleccion");

    private final String text;

    ModType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
