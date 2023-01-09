package ru.dsci.poiservice.shell.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.dsci.poiservice.shell.services.ShelterPoiService;
import ru.dsci.poiservice.core.entities.Poi;
import ru.dsci.poiservice.core.entities.PoiType;
import ru.dsci.poiservice.core.entities.dtos.DtoOsmPoi;
import ru.dsci.poiservice.shell.scrapers.ScrapeYandexMapAddresses;
import ru.dsci.poiservice.core.services.OsmGeoService;
import ru.dsci.poiservice.core.services.PoiService;
import ru.dsci.poiservice.core.services.PoiTypeService;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShelterPoiServiceImpl implements ShelterPoiService {

    private final ScrapeYandexMapAddresses scrapeYandexMapPoi;
    private final OsmGeoService osmGeoService;
    private final PoiTypeService poiTypeService;
    private final PoiService poiService;
    private final ModelMapper modelMapper;

    @Override
    public void updateShelters(String url) {
        log.info("retrieving yandex map POIs: {}", url);
        scrapeYandexMapPoi.setUrl(url);
        List<String> yandexPois = scrapeYandexMapPoi.doScrape();
        log.info("retrieved {} items", yandexPois.size());
        PoiType poiType = poiTypeService.getByCode("shelter");
        int items = yandexPois.size();
        int errors = 0;
        int warnings = 0;
        String yandexPoi = null;
        for (int i = 0; i < items; i++) {
            try {
                DtoOsmPoi dtoOsmPoi = null;
                yandexPoi = yandexPois.get(i);
                String[] poiAddress = yandexPoi.split(":");
                String address = poiAddress.length == 1 ? poiAddress[0] : poiAddress[poiAddress.length - 1];
                address = address.trim().replace("\"", "");
                Poi poi = new Poi();
                poi.setPoiType(poiType);
                poi.setDescription(yandexPoi);
                poi.setAddress(address);
                try {
                    dtoOsmPoi = osmGeoService.getByAddress(address);
                    if (dtoOsmPoi != null)
                        modelMapper.map(dtoOsmPoi, poi);
                    else
                        throw new IOException("POI is empty");
                } catch (Exception e) {
                    warnings++;
                    log.warn("WARNING: POI [{}] is inconsistent: {}", poi, e.getMessage());
                }
                log.info("[{}/{}/{}/{}] updating: {}",
                        i + 1,
                        warnings,
                        errors,
                        items,
                        poi);
                poiService.update(poi);
            } catch (Exception e) {
                errors++;
                log.error("[{}/{}/{}/{}] ERROR poi yandex processing [{}]: {}",
                        i + 1,
                        warnings,
                        errors,
                        items,
                        yandexPoi,
                        e.getMessage());
            }
        }
        log.info("processed: {} items", items);
        if (errors > 0)
            log.error("processed with errors: {} items", errors);
        if (warnings > 0)
            log.warn("processed with warnings: {} items", warnings);
    }

}
