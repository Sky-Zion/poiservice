package ru.dsci.poiservice.bot.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.dsci.poiservice.bot.dtos.PoiDistance;
import ru.dsci.poiservice.bot.dtos.PoiDistanceList;
import ru.dsci.poiservice.bot.services.BotShelterService;
import ru.dsci.poiservice.core.entities.Poi;
import ru.dsci.poiservice.core.entities.PoiType;
import ru.dsci.poiservice.core.geomath.Point;
import ru.dsci.poiservice.core.services.PoiService;
import ru.dsci.poiservice.core.services.PoiTypeService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BotShelterServiceImpl implements BotShelterService {

    public static final String POI_TYPE_CODE = "shelter";
    private static final String SEEK_RANGE_LIST = "100, 250, 500, 1000";

    private final PoiService poiService;
    private final PoiTypeService poiTypeService;

    private List<Double> seekRangeList = new ArrayList<>();

    @Value("${poi.seek_range_list}")
    private String seekRangeListSetting;

    @PostConstruct
    private void init() {
        if (seekRangeListSetting == null || seekRangeListSetting.length() < 1)
            seekRangeListSetting = SEEK_RANGE_LIST;
        seekRangeList = Arrays.stream(seekRangeListSetting.split(","))
                .map(s -> Double.valueOf(s.trim()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Poi> getAllByPoiTypeCodeAndPoint(String poiTypeCode, Point center) {
        PoiType poiType = poiTypeService.getByCodeNotFoundError(poiTypeCode);
        return poiService.getAllByPoiTypeAndRangeList(poiType, center, seekRangeList);
    }

    @Override
    public List<Poi> getAllByPoiTypeCodeAndAddress(String poiTypeCode, String address) {
        PoiType poiType = poiTypeService.getByCodeNotFoundError(poiTypeCode);
        Poi poi = poiService.getByPoiTypeAndAddress(poiType, address);
        return poiService.getAllByPoiTypeAndRangeList(
                poiType,
                new Point(poi.getGeoLat().doubleValue(), poi.getGeoLon().doubleValue()),
                seekRangeList);
    }

    public PoiDistanceList getAllNearLocation(Point location) {
        List<Poi> poiList = getAllByPoiTypeCodeAndPoint(POI_TYPE_CODE, location);
        PoiDistanceList poiDistanceList = new PoiDistanceList();
        if (poiList.size() > 0) {
            for (int i = 0; i < poiList.size(); i++) {
                poiDistanceList.addPoiDistance(new PoiDistance(location, poiList.get(i)));
            }
            poiDistanceList.sort();
        }
        return poiDistanceList;
    }

    public PoiDistanceList getAllNearLocation(Point location, int size) {
        PoiDistanceList poiDistanceList = getAllNearLocation(location);
        poiDistanceList.truncateSizeTo(size);
        return poiDistanceList;
    }

}
