package command;

import command.screen.RedirectCommand;
import dataTransportLayer.EventBuffer;

public class BackNavigation {

    private final EventBuffer targetBuffer;
    private final ICommand command;

    public BackNavigation(EventBuffer targetBuffer, ICommand command) {
        this.targetBuffer = targetBuffer;
        this.command = command;
    }

    public void go(EventBuffer currentBuffer) {
        currentBuffer.publish(new RedirectCommand(targetBuffer, command));
    }
}
