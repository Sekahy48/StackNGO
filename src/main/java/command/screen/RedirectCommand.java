package command.screen;

import command.ICommand;
import dataTransportLayer.EventBuffer;
import mvc.controller.AbstractController;

public class RedirectCommand implements ICommand {

    private ICommand command;
    private EventBuffer eventBuffer;

    public RedirectCommand(EventBuffer buffer, ICommand command) {
        this.command = command;
        this.eventBuffer = buffer;
    }

    @Override
    public void execute(AbstractController controller) {
        this.eventBuffer.publish(command);
    }

    @Override
    public void clear() {
        this.command = null;
        this.eventBuffer = null;
    }
}
