package command.screen;

import dataTransportLayer.EntryDTO;
import identificators.EntryId;
import mvc.controller.AbstractController;
import mvc.view.AbstractView;
import mvc.view.ViewType;

public class ChangeToCommand extends ChangeScreenCommand{

    private EntryDTO dto;
    private String accountName;
    
    public ChangeToCommand(ViewType type, EntryDTO collDto) {
        super(type); 
        this.dto = collDto;
    }

    public ChangeToCommand(ViewType type, String collDto) {
        super(type);
        this.accountName = collDto;
    }

    @Override
    public void execute(AbstractController controller){
        super.execute(controller);
        AbstractView addView = controller.getRuntimeContext().getScreenManager().getView(view);
        if (dto != null) addView.setParentId(new EntryId(dto.id));
        addView.setParentName(accountName );
    }
}