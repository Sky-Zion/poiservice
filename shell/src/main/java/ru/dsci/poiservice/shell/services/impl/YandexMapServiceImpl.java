package ru.dsci.poiservice.shell.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.dsci.poiservice.core.entities.Poi;
import ru.dsci.poiservice.core.entities.PoiType;
import ru.dsci.poiservice.core.entities.dtos.DtoPoi;
import ru.dsci.poiservice.core.services.GeoServiceImpl;
import ru.dsci.poiservice.core.services.PoiService;
import ru.dsci.poiservice.core.services.PoiTypeService;
import ru.dsci.poiservice.shell.scrapers.ScrapeYandexMapAddresses;
import ru.dsci.poiservice.shell.services.YandexMapService;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class YandexMapServiceImpl implements YandexMapService {

    public static final String ADDRESS_REGEX =
            ".*(д.|дом|ул.|улица|пр.|проспект|пер.|переулок|с.|село|п.|посёлок|поселок|г.|город|образование|р-н|район|обл.|область).*";

    private final ScrapeYandexMapAddresses scrapeYandexMapPoi;
    private final GeoServiceImpl osmGeoService;
    private final PoiTypeService poiTypeService;
    private final PoiService poiService;
    private final ModelMapper modelMapper;

    @Override
    public List<String> getItemsFromYandexMap(String url) {
        scrapeYandexMapPoi.setTimeout(30000);
        scrapeYandexMapPoi.setUrl(url);
        return scrapeYandexMapPoi.doScrape();
    }

    @Override
    public void updatePoiFromYandexMap(String poiTypeCode, String url) {
        updatePoiFromYandexMap(poiTypeCode, url, null);
    }

    private String getAddress(String mapAddress, String prefix) {
        String address;
        mapAddress = mapAddress.trim().replace("\"", "");
        String[] addressess = mapAddress.split("\n");
        for (int i = 0; i < addressess.length; i++) {
            String[] possibleAddress = addressess[i].split(":");
            for (int j = 1; j < possibleAddress.length; j++) {
                if (possibleAddress[j].matches(ADDRESS_REGEX)) {
                    address = prefix == null ? possibleAddress[j] : String.format("%s, %s", prefix, possibleAddress[j].trim());
                    return address;
                }
            }
        }
        return null;
    }

    @Override
    public void updatePoiFromYandexMap(String poiTypeCode, String url, String prefix) {
        PoiType poiType = poiTypeService.getByCodeNotFoundError(poiTypeCode);
        List<String> yandexPois = getItemsFromYandexMap(url);
        int items = yandexPois.size();
        int errors = 0;
        int warnings = 0;
        String yandexPoiAddress = null;
        for (int i = 0; i < items; i++) {
            try {
                DtoPoi dtoOsmPoi;
                yandexPoiAddress = yandexPois.get(i);
                String address = getAddress(yandexPoiAddress, prefix);
                Poi poi = new Poi();
                poi.setPoiType(poiType);
                poi.setDescription(yandexPoiAddress);
                poi.setAddress(address);
                try {
                    if (poi.getAddress() == null)
                        throw new IOException("address is empty");
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
                        yandexPoiAddress,
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
