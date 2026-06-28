package service;

import java.util.List;

import dataAccessLayer.DAO.AbstractEntryDAO;
import dataTransportLayer.CollectionDTO;
import dataTransportLayer.ComponentDefinitionDTO;
import dataTransportLayer.ItemDTO;
import identificators.EntryId;
import mvc.context.DataContext;
import mvc.model.entries.Item;
import mvc.model.entries.component.ComponentDefinition;

public class ComponentService extends AbstractEntryService<ComponentDefinitionDTO, ComponentDefinition>{
    
    public ComponentService(DataContext data) {
        super(data); 
    }

    @Override
    public ServiceType getType() {
        return ServiceType.COMPONENT;
    }

    @Override
    protected ComponentDefinition createEntry(ComponentDefinitionDTO dto) {
        return this.entriesFactory.createComponent(dto);
    }

    @Override
    protected  boolean addConcreteEntry(ComponentDefinition entry) {
        return this.data.getEntriesRepo().addComponent(entry);
    }

    @Override 
    protected ComponentDefinition getConcreteEntry(int id) {
        return this.data.getEntriesRepo().getComponent(new EntryId(id));
    }

    @Override 
    protected ComponentDefinition getConcreteEntryByName(String name) {
        return this.data.getEntriesRepo().getComponentByName(name);
    }

    @Override
    protected AbstractEntryDAO<ComponentDefinitionDTO, ComponentDefinition> getDAO() {
        return this.data.getComponentDAO();
    }
}
