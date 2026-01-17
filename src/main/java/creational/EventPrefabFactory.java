package creational;

import java.util.List;
 
import dataTransportLayer.EntryDTO;
import dataTransportLayer.GenericDTO;
import dataTransportLayer.ItemStackDTO;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox; 

public class EventPrefabFactory {

    public static EventHandler<ActionEvent> getDeleteSelfRowEvent(VBox table, HBox row, List<GenericDTO> list, EntryDTO toRm){
        return e -> {
                    table.getChildren().remove(row);
                    list.remove(toRm);
                };
    }

    public static EventHandler<ActionEvent> getDeleteSelfRowEvent(VBox table, HBox row, List<GenericDTO> list, ItemStackDTO toRm){
        return e -> {
                    table.getChildren().remove(row);
                    list.remove(toRm);
                };
    }
}
