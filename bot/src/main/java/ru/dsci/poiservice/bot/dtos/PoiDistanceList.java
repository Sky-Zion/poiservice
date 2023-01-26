package ru.dsci.poiservice.bot.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PoiDistanceList {

    private List<PoiDistance> poiDistanceList = new ArrayList<>();

    public void addPoiDistance(PoiDistance poiDistance) {
        poiDistanceList.add(poiDistance);
    }

    public void sort () {
        poiDistanceList.sort(PoiDistance.getComparator());
    }

    public int size () {
        return  poiDistanceList.size();
    }

    public PoiDistance get(int i) {
        return poiDistanceList.get(i);
    }

    public List<PoiDistance> truncateSizeTo(int size) {
        if (poiDistanceList.size() > size)
            poiDistanceList = poiDistanceList.subList(0, size);
        return poiDistanceList;
    }

}
