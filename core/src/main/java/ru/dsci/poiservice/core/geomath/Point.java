package ru.dsci.poiservice.core.geomath;

import java.text.DecimalFormat;
import ru.dsci.poiservice.core.Constants;

public class Point {

    private final Double lat;
    private final Double lon;

    private DecimalFormat format = Constants.GEO_FORMAT;

    public Point (Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public DecimalFormat getFormat() {
        return format;
    }

    public void setFormat(DecimalFormat format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return String.format("%s,%s", format.format(lat), format.format(lon));
    }

}
