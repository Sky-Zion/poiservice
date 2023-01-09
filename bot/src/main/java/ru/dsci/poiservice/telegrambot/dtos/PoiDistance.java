package ru.dsci.poiservice.telegrambot.dtos;

import ru.dsci.poiservice.core.entities.Poi;
import ru.dsci.poiservice.core.geomath.GeoTools;
import ru.dsci.poiservice.core.geomath.Point;

import java.util.Comparator;

public class PoiDistance implements Comparable<PoiDistance> {

    private Poi poi;
    private Point point;
    private double distance;

    private final static Comparator<PoiDistance> comparator = Comparator.comparingDouble(PoiDistance::getDistance);

    public static Comparator<PoiDistance> getComparator() {
        return comparator;
    }

    @Override
    public int compareTo(PoiDistance o) {
        return comparator.compare(this, o);
    }

    public Poi getPoi() {
        return poi;
    }

    public Point getPoint() {
        return point;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return String.format("%s: %s m", poi, distance);
    }

    public PoiDistance(Point point, Poi poi) {
        this.point = point;
        this.poi = poi;
        this.distance = GeoTools.getDistance(point, new Point(poi.getGeoLat().doubleValue(), poi.getGeoLon().doubleValue()));
    }

}
