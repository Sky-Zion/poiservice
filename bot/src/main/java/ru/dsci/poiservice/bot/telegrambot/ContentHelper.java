package ru.dsci.poiservice.bot.telegrambot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Location;
import ru.dsci.poiservice.bot.dtos.PoiDistance;
import ru.dsci.poiservice.bot.dtos.PoiDistanceList;
import ru.dsci.poiservice.bot.services.BotShelterService;
import ru.dsci.poiservice.core.StringTools;
import ru.dsci.poiservice.core.entities.Poi;
import ru.dsci.poiservice.core.geomath.Point;

@Slf4j
@RequiredArgsConstructor
@Component
public class ContentHelper {

    private final int TRIM_ADDRESS_ENTRIES = 2;

    private final String ADDRESS_DELIMITER = ",";

    private final Constants constants;

    private final BotShelterService botShelterService;

    @Value("#{new Integer('${poi.max_results}')}")
    private Integer maxResults;

    public String trimAddress(String address) {
        return StringTools.trimEntries(address, TRIM_ADDRESS_ENTRIES, ADDRESS_DELIMITER).trim();
    }

    public String getAddressTag(Poi poi) {
        return String.format(constants.getBlankGmapATag(),
                poi.getGeoLat(),
                poi.getGeoLon(),
                poi.getGeoLat(),
                trimAddress(poi.getAddress()));
    }

    public String getSheltersTextByLocation(Location location) {
        PoiDistanceList poiDistanceList;
        Point userLocation = new Point(location.getLatitude(), location.getLongitude());
        poiDistanceList = botShelterService.getAllNearLocation(userLocation, maxResults);
        Poi nearestPoi;
        StringBuilder text = new StringBuilder();
        if (poiDistanceList.size() > 0) {
            text.append(String.format("\uD83D\uDEA8Найдены укрытия (%d м):\n",
                    (int) poiDistanceList.get(poiDistanceList.size() - 1).getDistance()));
            for (int i = 0; i < poiDistanceList.size(); i++) {
                PoiDistance poiDistance = poiDistanceList.get(i);
                text.append(String.format("%d:\uD83D\uDCCD%s (%d м)\n",
                        i + 1,
                        getAddressTag(poiDistance.getPoi()),
                        (int) (poiDistance.getDistance())));
            }
            nearestPoi = poiDistanceList.get(0).getPoi();
            text.append(String.format("\uD83C\uDFE0Ближайшее укрытие:\n%s\n\uD83D\uDCCD%s",
                    nearestPoi.getDescription(),
                    getAddressTag(nearestPoi)
                    ));
        } else {
            String response = "\uD83D\uDE16Укрытия поблизости не найдены";
            text.append(response);
        }
        return text.toString();
    }
}
