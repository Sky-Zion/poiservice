package ru.dsci.poiservice.bot.services;

import ru.dsci.poiservice.bot.dtos.PoiDistanceList;
import ru.dsci.poiservice.core.entities.Poi;
import ru.dsci.poiservice.core.geomath.Point;

import java.util.List;

public interface BotShelterService {

    List<Poi> getAllByPoiTypeCodeAndPoint(String poiTypeCode, Point center);

    List<Poi> getAllByPoiTypeCodeAndAddress (String poiTypeCode, String address);

    PoiDistanceList getAllNearLocation(Point location);

    PoiDistanceList getAllNearLocation(Point location, int size);

}
