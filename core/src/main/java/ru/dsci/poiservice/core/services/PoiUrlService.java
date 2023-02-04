package ru.dsci.poiservice.core.services;

import ru.dsci.poiservice.core.entities.PoiUrl;

import java.util.List;

public interface PoiUrlService {

    PoiUrl getByCode(String mapCode);

    List<PoiUrl> getAll();

}
