package command.screen;

import command.ICommand;
import dataTransportLayer.EventBuffer;
import mvc.controller.AbstractController;
import command.AbstractControllerCommand;

public class RedirectCommand extends AbstractControllerCommand {

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
