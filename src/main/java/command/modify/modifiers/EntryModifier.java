 package command.modify.modifiers;

import dataTransportLayer.EntryDTO;
import mvc.controller.AbstractController;

public interface EntryModifier {
    void modify(EntryDTO dto, AbstractController controller);
}
