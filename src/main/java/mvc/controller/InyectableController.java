package mvc.controller;

import java.util.List;

import dataTransportLayer.EntryDTO;

public interface InyectableController {
    public void setListWhereAdd(List<EntryDTO> list);
}
