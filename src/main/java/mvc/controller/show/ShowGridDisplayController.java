package mvc.controller.show;

import java.util.List;

import command.ICommand;
import dataTransportLayer.EventBuffer;
import dataTransportLayer.GenericDTO;
import mvc.controller.AbstractController;
import mvc.view.show.ShowGridDisplayView;

/**
 * Controlador base para vistas tipo grid.
 * Se encarga de poblar la vista con elementos y asignar comandos.
 */
public abstract class ShowGridDisplayController<T extends GenericDTO> extends AbstractController<ShowGridDisplayView<T>> {

    public ShowGridDisplayController(EventBuffer buffer) {
        super(buffer);
    }

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
                e -> buffer.publish(createCommand(element))
            );
        }
    }

    public void emptyGrid() {
        this.view.emptyGrid();
    }

    // --- Métodos abstractos que los hijos deben implementar ---

    /** Lista de elementos a mostrar */
    protected abstract List<T> getElements();

    /** Título/nombre del elemento */
    protected abstract String getTitle(T element);

    /** Ruta de imagen del elemento (puede ser null) */
    protected abstract String getImagePath(T element);

    /** Comando a lanzar al pulsar el elemento */
    protected abstract ICommand createCommand(T element);

    @Override
    public void handleButton() {
        commonHandleButton();
    }
}
