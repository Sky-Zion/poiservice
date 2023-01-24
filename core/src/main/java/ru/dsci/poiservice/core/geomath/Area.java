package ru.dsci.poiservice.core.geomath;

import ru.dsci.poiservice.core.Constants;

import java.util.ArrayList;
import java.util.List;

public class Area {

    private Point center;
    private double width;
    private double height;
    private List<Point> vertices = new ArrayList<>();

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public double getWidth() {
        return width;
    }

    public List<Point> getVertices() {
        return vertices;
    }

    public void setWidth(double width) {
        if (width <= 0) throw new IllegalArgumentException(
                String.format(
                        "width can't be: %s",
                        Constants.GEO_FORMAT.format(width)));
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        if (width <= 0) throw new IllegalArgumentException(
                String.format(
                        "height can't be: %s",
                        Constants.GEO_FORMAT.format(width)));
        this.height = height;
    }

    @Override
    public String toString() {
        return String.format(
                "%s; %s,%s", center,
                Constants.GEO_FORMAT.format(width),
                Constants.GEO_FORMAT.format(height));
    }

    private void setCones() {
        vertices.clear();
        double hw = width / 2;
        double hh = height / 2;
        vertices.add(GeoTools.pointOffset(center, -hw, -hh));
        vertices.add(GeoTools.pointOffset(center, -hw, hh));
        vertices.add(GeoTools.pointOffset(center, hw, hh));
        vertices.add(GeoTools.pointOffset(center, hw, -hh));
    }

    public Area(Point center, double width, double height) {
        this.center = center;
        this.width = width;
        this.height = height;
        setCones();
    }

}
