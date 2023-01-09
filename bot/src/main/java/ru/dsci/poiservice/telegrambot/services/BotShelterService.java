package ru.dsci.poiservice.telegrambot.services;

import ru.dsci.poiservice.core.entities.Poi;
import ru.dsci.poiservice.core.geomath.Point;
import ru.dsci.poiservice.telegrambot.dtos.PoiDistanceList;

import java.util.List;

public interface BotShelterService {

    List<Poi> getAllByPoiTypeCodeAndPoint(String poiTypeCode, Point center);

    List<Poi> getAllByPoiTypeCodeAndAddress (String poiTypeCode, String address);

    PoiDistanceList getAllNearLocation(Point location);

}
