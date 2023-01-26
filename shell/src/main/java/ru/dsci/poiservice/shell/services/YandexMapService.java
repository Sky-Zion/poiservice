package ru.dsci.poiservice.shell.services;

import java.util.List;

public interface YandexMapService {

    void updatePoiFromYandexMap(String poiTypeCode, String url);

    void updatePoiFromYandexMap(String poiTypeCode, String url, String prefix);

    List<String> getItemsFromYandexMap(String url);

}
