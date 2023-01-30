package ru.dsci.poiservice.core.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.dsci.poiservice.core.StringTools;
import ru.dsci.poiservice.core.clients.HttpClientImpl;
import ru.dsci.poiservice.core.entities.dtos.DtoPoi;
import ru.dsci.poiservice.core.services.GeoServiceImpl;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpResponse;

@Slf4j
@RequiredArgsConstructor
@Service
public class GeocoderGeoServiceImpl implements GeoServiceImpl {

    private final static String URL_BLANK = "https://catalog.api.2gis.com/3.0/items/geocode?q=%s&fields=items.point&key=%s";

    private final HttpClientImpl httpClient;

    @Value("${geocoder.key}")
    private String key;

    private DtoPoi getDtoGeocoderPoi(JSONObject responseData) {
        DtoPoi dtoOsmPoi = null;
        try {
            if (responseData.has("id")
                    && responseData.has("point")
                    && responseData.has("full_name")) {
                dtoOsmPoi = new DtoPoi();
                dtoOsmPoi.setGeoId(responseData.getLong("id"));
                dtoOsmPoi.setAddress(String.format("Россия, %s", responseData.getString("full_name")));
                JSONObject point = responseData.getJSONObject("point");
                dtoOsmPoi.setGeoLon(BigDecimal.valueOf(point.getDouble("lon")));
                dtoOsmPoi.setGeoLat(BigDecimal.valueOf(point.getDouble("lat")));
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return dtoOsmPoi;
    }

    @Override
    public DtoPoi getByAddress(String address) throws EntityNotFoundException {
        DtoPoi dtoPoi = null;
        try {
            address = address.trim();
            String url = String.format(URL_BLANK, address.replace(" ", "+"), key);
            url = StringTools.cleanUrl(url);
            HttpResponse<String> response = httpClient.getResponse(url);
            if (response.body() == null || response.body().equals("[]"))
                throw new IOException("response body is empty");
            JSONArray responseDataArray = new JSONObject(response.body()).getJSONObject("result").getJSONArray("items");
            for (int i = 0; i < responseDataArray.length(); i++) {
                try {
                    JSONObject responseData = responseDataArray.getJSONObject(i);
                    log.debug(responseData.toString());
                    dtoPoi = getDtoGeocoderPoi(responseData);
                    if (dtoPoi != null)
                        break;
                } catch (Exception e) {
                    log.debug("address parsing error: {}", e.getMessage());
                }
            }
            if (dtoPoi == null) {
                JSONObject responseData = new JSONObject(response.body().substring(1, response.body().length() - 1));
                dtoPoi = getDtoGeocoderPoi(responseData);
            }
            if (dtoPoi == null) {
                throw new IOException("POI address parsing failed");
            }
        } catch (Exception e) {
            throw new EntityNotFoundException(String.format("unable get POI by address (%s): %s", address, e.getMessage()));
        }
        return dtoPoi;
    }

    public String getKey() {
        return key;
    }

}
