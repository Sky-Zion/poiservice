package ru.dsci.poiservice.core.geomath;

import static java.lang.Math.*;

public class GeoTools {

    public static final double EARTH_R = 6378137;

    public static double getDistance(double latA, double lonA, double latB, double lonB) {
        double radLatA = Math.toRadians(latA);
        double radLatB = Math.toRadians(latB);
        double radLonA = Math.toRadians(lonA);
        double radLonB = Math.toRadians(lonB);
        return Math.acos(sin(radLatA) * sin(radLatB) + cos(radLatA) * cos(radLatB) * cos(radLonB - radLonA)) * EARTH_R;
    }

    public static double getDistance(Point pointA, Point pointB) {
        return getDistance(pointA.getLat(), pointA.getLon(), pointB.getLat(), pointB.getLon());
    }

    public static double distanceToLatRad(double distance) {
        return distance / EARTH_R;
    }

    public static double distanceToLonRad(double distance, double lat) {
        return distance / (EARTH_R * cos(lat));
    }

    public static double distanceToLatDeg(double distance) {
        return Math.toDegrees(distanceToLatRad(distance));
    }

    public static double distanceToLonDeg(double distance, double lat) {
        return Math.toDegrees(distanceToLonRad(distance, Math.toRadians(lat)));
    }

    public static Point movePoint(Point point, double dX, double dY) {
        double lat = point.getLat() + distanceToLatDeg(dX);
        return new Point(lat, point.getLon() + distanceToLonDeg(dY, lat));
    }

}
