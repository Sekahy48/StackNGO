    package service;

    import dataTransportLayer.EventBuffer;
    import mvc.context.SystemContext;
    import mvc.view.ViewType;

    public class ControllerService implements  IService{

        private SystemContext systemContext;

        public ControllerService(SystemContext context) {
            this.systemContext = context;
        }

        @Override
        public ServiceType getType() {
            return ServiceType.CONTROLLER;
        }

        public EventBuffer getControllerBuffer(ViewType type) {
            return this.systemContext.getController(type).getBuffer();
        }
    }
