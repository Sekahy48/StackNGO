package mvc.controller.show.single;
    
import javafx.scene.control.Button;
import mvc.controller.AbstractController; 
import mvc.view.show.entry.data.AbstractShowDataView;

public abstract class AbstractShowDataController<T extends AbstractShowDataView> extends AbstractController<T> {

    protected Button modifyButton;
    protected Button deleteButton;

    @Override 
    public void handleButtons() {
        this.view.getGoBackButton().setOnAction(e -> { this.onReturnEvent(); }); 
        this.view.getDeleteButton().setOnAction(e -> { 
            this.deleteShowingEntry(this.getShowingEntryId()); 
            this.onReturnEvent();
        });
    }

    @Override
    public void attachView(T view) {
        this.view =  view;
        super.attachView(view); 
    }

    public abstract void deleteShowingEntry(int id);
    public abstract int getShowingEntryId();
 
}