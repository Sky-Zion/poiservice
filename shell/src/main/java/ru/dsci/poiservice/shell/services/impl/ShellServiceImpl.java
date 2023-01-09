package ru.dsci.poiservice.shell.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import ru.dsci.poiservice.core.entities.Poi;
import ru.dsci.poiservice.core.entities.PoiType;
import ru.dsci.poiservice.core.services.*;
import ru.dsci.poiservice.shell.services.*;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@DependsOn({"poiTypeService"})
public class ShellServiceImpl implements ShellService {
    private final PoiTypeService poiTypeService;
    private final PoiService poiService;
    private final OsmGeoService osmGeoService;
    private final ShelterPoiService shelterPoiService;
    private final ModelMapper modelMapper;

    @Override
    public void updateShelters(String url) {
        shelterPoiService.updateShelters(url);
    }

    @Override
    public List<PoiType> getPoiTypes() {
        return poiTypeService.getAll();
    }

    @Override
    @Transactional
    public PoiType updatePoiType(String code, String title, String description) {
        PoiType poiType = new PoiType();
        poiType.setCode(code);
        poiType.setTitle(title);
        poiType.setDescription(description == null ? title : description);
        return poiTypeService.update(poiType);
    }

    @Override
    @Transactional
    public Poi updatePoi(String typeCode,
                         String address,
                         String description,
                         BigDecimal geoLat,
                         BigDecimal geoLong) {
        PoiType poiType = poiTypeService.getByCode(typeCode);
        Poi poi = new Poi();
        poi.setPoiType(poiType);
        poi.setAddress(address);
        poi.setDescription(description);
        poi.setGeoLat(geoLat);
        poi.setGeoLon(geoLong);
        return poiService.update(poi);
    }

    @Override
    public Poi updatePoiOsm(String typeCode, String address, String description) throws IOException {
        PoiType poiType = poiTypeService.getByCode(typeCode);
        Poi poi = new Poi();
        modelMapper.map(osmGeoService.getByAddress(address), poi);
        poi.setPoiType(poiType);
        if (description != null)
            poi.setDescription(description);
        return poiService.update(poi);
    }

    @Override
    public List<Poi> getPoiByType(String poiTypeCode) {
        PoiType poiType = poiTypeService.getByCode(poiTypeCode);
        if (poiType == null)
            throw new EntityNotFoundException(
                    String.format("POI type with code [%s] not found", poiTypeCode));
        return poiService.getAllByPoiType(poiType);
    }
}