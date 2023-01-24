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

    public static double latOffsetRad(double dX) {
        return dX / EARTH_R;
    }

    public static double lonOffsetToRad(double dY, double lat) {
        return dY / (EARTH_R * cos(lat));
    }

    public static double latOffsetToDeg(double dX) {
        return Math.toDegrees(latOffsetRad(dX));
    }

    public static double lonOffsetToDeg(double dY, double lat) {
        return Math.toDegrees(lonOffsetToRad(dY, Math.toRadians(lat)));
    }

    public static Point pointOffset(Point point, double dX, double dY) {
        double lat = point.getLat() + latOffsetToDeg(dX);
        return new Point(lat, point.getLon() + lonOffsetToDeg(dY, lat));
    }

}
