package ru.dsci.poiservice.core.services;

import ru.dsci.poiservice.core.entities.Poi;
import ru.dsci.poiservice.core.entities.PoiType;
import ru.dsci.poiservice.core.geomath.Area;
import ru.dsci.poiservice.core.geomath.Point;

import java.util.List;

public interface PoiService {

    List<Poi> getAllByPoiType(PoiType poiType);

    List<Poi> getAllByPoiTypeAndArea(PoiType poiType, Area area);

    List<Poi> getAllByPoiTypeAndArea(PoiType poiType, Point center, double areaWidth, double areaHeight);

    List<Poi> getAllByPoiTypeAndRange(PoiType poiType, Point center, double range);

    List<Poi> getAllByPoiTypeAndRangeList(PoiType poiType, Point center, List<Double> rangeList);

    Poi getByPoiTypeAndOsmId(PoiType poiType, Long osmId);

    Poi getByPoiTypeAndAddress(PoiType poiType, String address);

    Poi getByPoiTypeAndDescription(PoiType poiType, String description);

    Poi update(Poi poi);

}
