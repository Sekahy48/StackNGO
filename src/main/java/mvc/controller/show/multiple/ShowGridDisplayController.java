package mvc.controller.show.multiple;
 
import java.util.List; 
import dataTransportLayer.GenericDTO; 
import mvc.controller.AbstractController;
import mvc.view.show.multiple.ShowGridDisplayView;

/**
 * Controlador base para vistas tipo grid.
 * Se encarga de poblar la vista con elementos y asignar comandos.
 */
public abstract class ShowGridDisplayController<T extends GenericDTO> extends AbstractController<ShowGridDisplayView<T>> {

    @Override
    public void attachView(ShowGridDisplayView<T> view) {
        this.view = view;
        super.attachView(view);
    }

    /** Template Method: pobla la grid con los elementos y les asigna comandos */
    public void populateGrid() {
        emptyGrid();
        for (T element : getElements()) {
            this.view.addElementToGrid(
                element,
                e -> {this.onClickElementEvent(element);}
            );
        }
    }

    public void emptyGrid() {
        this.view.emptyGrid();
    }

    // --- Métodos abstractos que los hijos deben implementar ---

    /** Lista de elementos a mostrar */
    protected abstract List<T> getElements();

    /** Comando a lanzar al pulsar el elemento */
    protected abstract void onClickElementEvent(T dto); 

    @Override
    public void handleButtons() {
        commonHandleButton();
    }

    @Override 
    public void updateAtShow() {
        this.populateGrid();
    }
}
