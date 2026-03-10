package command;

import service.ControllerService;

public abstract class AbstractControllerCommand implements ICommand{
    protected static ControllerService service;

    private static void init(ControllerService inService) {
        if (service != null) {
            throw new IllegalStateException("El servicio interno de los comandos de tipo" +
                                            AbstractControllerCommand.class + 
                                            "ya ha sido determiando previamente y no puede ser modificado");
        } else {
            service = inService;
        }
    }

}    
