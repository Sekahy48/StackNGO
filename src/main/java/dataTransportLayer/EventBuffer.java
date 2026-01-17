package dataTransportLayer;

import command.ICommand; 
import observer.AbstractSubject;

import java.util.*;

public class EventBuffer extends AbstractSubject<EventBuffer> {

    private final ArrayDeque<ICommand> commands;

    public EventBuffer() {
        commands = new ArrayDeque<>();
    }

    public void publish(ICommand command){
        commands.add(command);
        notifyObservers(this);
    }

    public List<ICommand> drain() {
        List<ICommand> drained = new ArrayList<>(commands);
        commands.clear();
        return drained;
    }



}

