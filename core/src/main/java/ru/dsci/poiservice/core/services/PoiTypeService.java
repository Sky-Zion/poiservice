package ru.dsci.poiservice.core.services;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.dsci.poiservice.core.entities.PoiType;

import java.util.List;

@Component
@Service
public interface PoiTypeService {

    PoiType getByCode(String typeCode);

    PoiType getByCodeNotFoundError(String typeCode);

    PoiType update(PoiType poiType);

    List<PoiType> getAll();

}
