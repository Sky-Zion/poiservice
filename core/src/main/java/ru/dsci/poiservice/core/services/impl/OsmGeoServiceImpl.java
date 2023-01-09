package ru.dsci.poiservice.core.services.impl;

import ru.dsci.poiservice.core.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import ru.dsci.poiservice.core.clients.HttpClientImpl;
import ru.dsci.poiservice.core.entities.dtos.DtoOsmPoi;
import ru.dsci.poiservice.core.services.OsmGeoService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@RequiredArgsConstructor
@Service
@Slf4j
public class OsmGeoServiceImpl implements OsmGeoService {

    private final static String URL_BLANK = "https://nominatim.openstreetmap.org/search?q=%s&format=json&addressdetails=1";

    private final HttpClientImpl httpClientImpl;

    private HttpResponse<String> getResponse(String url) throws IOException {
        HttpResponse<String> response;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .build();
            response = httpClientImpl.getClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300)
                throw new IOException(String.format("response status: %d", response.statusCode()));
        } catch (InterruptedException | IOException e) {
            throw new IOException(String.format("bad response [%s]: %s", url, e.getMessage()));
        }
        return response;
    }

    private DtoOsmPoi getDtoOsmPoi(JSONObject responseData) throws IOException {
        DtoOsmPoi dtoOsmPoi = null;
        if (!responseData.has("address"))
            throw new IOException("POI address is not defined");
        JSONObject addressData = responseData.getJSONObject("address");
        try {
            if (responseData.has("osm_id")
                    && responseData.has("lat")
                    && responseData.has("lon")
                    && addressData.has("country")
                    && addressData.has("city")
                    && addressData.has("road")
                    && addressData.has("house_number")) {
                dtoOsmPoi = new DtoOsmPoi();
                dtoOsmPoi.setGeoId(responseData.getLong("osm_id"));
                dtoOsmPoi.setGeoLat(BigDecimal.valueOf(responseData.getFloat("lat")));
                dtoOsmPoi.setGeoLon(BigDecimal.valueOf(responseData.getFloat("lon")));
                dtoOsmPoi.setAddress(String.format("%s, %s, %s, %s",
                        addressData.getString("country"),
                        addressData.getString("city"),
                        addressData.getString("road"),
                        addressData.getString("house_number")));
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return dtoOsmPoi;
    }

    @Override
    public DtoOsmPoi getByAddress(String address) throws EntityNotFoundException {
        DtoOsmPoi dtoOsmPoi = null;
        try {
            address = address.trim();
            String url = String.format(URL_BLANK, address.replace(" ", "+"));
            url = StringTools.cleanUrl(url);
            HttpResponse<String> response = getResponse(url);
            if (response.body() == null || response.body().equals("[]"))
                throw new IOException("response body is empty");
            JSONArray responseDataArray = new JSONArray(response.body());
            for (int i = 0; i < responseDataArray.length(); i++) {
                try {
                    JSONObject responseData = responseDataArray.getJSONObject(i);
                    dtoOsmPoi = getDtoOsmPoi(responseData);
                    if (dtoOsmPoi != null)
                        break;
                } catch (Exception e) {
                    log.debug("address parsing error: {}", e.getMessage());
                }
            }
            if (dtoOsmPoi == null) {
                JSONObject responseData = new JSONObject(response.body().substring(1, response.body().length() - 1));
                dtoOsmPoi = getDtoOsmPoi(responseData);
            }
            if (dtoOsmPoi == null) {
                throw new IOException("POI address parsing failed");
            }
        } catch (Exception e) {
            throw new EntityNotFoundException(String.format("unable get POI by address (%s): %s", address, e.getMessage()));
        }
        return dtoOsmPoi;
    }

}
