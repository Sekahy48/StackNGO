package service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServiceConsumer {
    private final Map<ServiceType, IService> services = new HashMap<>();

    public void addService(IService service){
        if (!this.services.values().contains(service)) {
            services.put(service.getType(), service);
        }
    }
 
    @SuppressWarnings("unchecked")
    protected <S extends IService> S getService(ServiceType type) {
        return (S) this.services.get(type);
    }

    protected boolean removeService(ServiceType type) {
        if (this.services.containsKey(type)) {
            this.services.remove(type);
            return true;
        }
        return false;
    }

    public Set<ServiceType> requiredServices() {
        return Set.of(); 
    }

}
