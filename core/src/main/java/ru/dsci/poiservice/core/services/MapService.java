package ru.dsci.poiservice.core.services;

import ru.dsci.poiservice.core.entities.Map;

import java.util.List;

public interface MapService {

    Map getByCode(String mapCode);

    List<Map> getAll();

}
