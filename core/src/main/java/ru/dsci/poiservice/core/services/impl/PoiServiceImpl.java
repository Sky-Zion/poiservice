package ru.dsci.poiservice.core.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import ru.dsci.poiservice.core.entities.Poi;
import ru.dsci.poiservice.core.entities.PoiType;
import ru.dsci.poiservice.core.repositories.PoiRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import ru.dsci.poiservice.core.services.PoiService;
import org.springframework.stereotype.Service;
import ru.dsci.poiservice.core.geomath.Area;
import ru.dsci.poiservice.core.geomath.Point;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@DependsOn({"poiRepository", "modelMapper"})
public class PoiServiceImpl implements PoiService {

    private final PoiRepository poiRepository;
    private final ModelMapper modelMapper;

    @Override
    public Poi getByPoiTypeAndOsmId(PoiType poiType, Long osmId) {
        return poiRepository.getByPoiTypeAndOsmId(poiType, osmId);
    }

    @Override
    public Poi getByPoiTypeAndAddress(PoiType poiType, String address) {
        return poiRepository.getByPoiTypeAndAddressIgnoreCase(poiType, address);
    }

    @Override
    public Poi getByPoiTypeAndDescription(PoiType poiType, String description) {
        return poiRepository.getByPoiTypeAndDescriptionIgnoreCase(poiType, description);
    }

    @Override
    public Poi update(Poi poi) {
        if (poi == null)
            return null;
        Poi existingPoi = null;
        if (poi.getOsmId() != null)
            existingPoi = getByPoiTypeAndOsmId(poi.getPoiType(), poi.getOsmId());
        if (existingPoi == null && poi.getAddress() != null)
            existingPoi = getByPoiTypeAndAddress(poi.getPoiType(), poi.getAddress());
        if (existingPoi == null && poi.getDescription() != null)
            existingPoi = getByPoiTypeAndDescription(poi.getPoiType(), poi.getDescription());
        if (existingPoi == null)
            existingPoi = poiRepository.save(poi);
        else {
            if (!poi.equals(existingPoi)) {
                modelMapper.map(poi, existingPoi);
                poiRepository.save(existingPoi);
            }
        }
        return existingPoi;
    }

    @Override
    public List<Poi> getAllByPoiType(PoiType poiType) {
        return poiRepository.getAllByPoiType(poiType);
    }

    @Override
    public List<Poi> getAllByPoiTypeAndArea(PoiType poiType, Area area) {
        log.debug("{},{},{},{}", area.getVertices().get(0).getLat(), area.getVertices().get(1).getLat(), area.getVertices().get(0).getLon(), area.getVertices().get(2).getLon());
        return (poiRepository.getAllByPoiTypeAndGeoLatIsBetweenAndGeoLonIsBetween(
                poiType,
                BigDecimal.valueOf(area.getVertices().get(0).getLat()),
                BigDecimal.valueOf(area.getVertices().get(2).getLat()),
                BigDecimal.valueOf(area.getVertices().get(0).getLon()),
                BigDecimal.valueOf(area.getVertices().get(1).getLon())));
    }

    @Override
    public List<Poi> getAllByPoiTypeAndArea(PoiType poiType, Point areaCenter, double areaWidth, double areaHeight) {
        Area area = new Area(areaCenter, areaWidth, areaHeight);
        return getAllByPoiTypeAndArea(poiType, area);
    }

    @Override
    public List<Poi> getAllByPoiTypeAndRange(PoiType poiType, Point center, double range) {
        Area area = new Area(center, range, range);
        log.debug("range: {}", range);
        log.debug("area: {}: {}", area, area.getVertices());
        return getAllByPoiTypeAndArea(poiType, area);
    }

    @Override
    public List<Poi> getAllByPoiTypeAndRangeList(PoiType poiType, Point center, List<Double> rangeList) {
        List<Poi> poiList = new ArrayList<>();
        for (int i = 0; i < rangeList.size(); i++) {
            poiList = getAllByPoiTypeAndRange(poiType, center, rangeList.get(i));
            if (poiList.size() > 0)
                break;
        }
        return poiList;
    }


}
