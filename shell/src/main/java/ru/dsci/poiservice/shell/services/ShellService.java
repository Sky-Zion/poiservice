package ru.dsci.poiservice.shell.services;

import ru.dsci.poiservice.core.entities.Poi;
import ru.dsci.poiservice.core.entities.PoiType;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface ShellService {

    void updateShelters(String url);

    List<PoiType> getPoiTypes();

    Poi updatePoi(String typeCode,
                  String address,
                  String description,
                  BigDecimal geoLat,
                  BigDecimal geoLong);

    Poi updatePoiOsm(String typeCode, String address, String description) throws IOException;

    PoiType updatePoiType(String code, String title, String description);

    List<Poi> getPoiByType(String poiType);

}
