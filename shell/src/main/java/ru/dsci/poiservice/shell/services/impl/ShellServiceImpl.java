package ru.dsci.poiservice.shell.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import ru.dsci.poiservice.core.entities.Poi;
import ru.dsci.poiservice.core.entities.PoiType;
import ru.dsci.poiservice.core.services.GeoService;
import ru.dsci.poiservice.core.services.PoiService;
import ru.dsci.poiservice.core.services.PoiTypeService;
import ru.dsci.poiservice.shell.services.ShellService;
import ru.dsci.poiservice.shell.services.YandexMapService;

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
    private final GeoService geoService;
    private final YandexMapService yandexMapService;
    private final ModelMapper modelMapper;

    @Override
    public void updatePoisFromYandexMap(String poiTypeCode, String url) {
        yandexMapService.updatePoiFromYandexMap(poiTypeCode, url);
    }

    @Override
    public void updatePoisFromYandexMap(String poiTypeCode, String url, String prefix) {
        yandexMapService.updatePoiFromYandexMap(poiTypeCode, url);
    }

    @Override
    public List<String> getItemsFromYandexMap(String url) {
        log.info("retrieving yandex map items: {}", url);
        List<String> mapItems = yandexMapService.getItemsFromYandexMap(url);
        log.info("retrieved {} items", mapItems.size());
        return mapItems;
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
    public Poi updatePoiOsm(String poiTypeCode, String address, String description) throws IOException {
        PoiType poiType = poiTypeService.getByCode(poiTypeCode);
        Poi poi = new Poi();
        modelMapper.map(geoService.getByAddress(address), poi);
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
