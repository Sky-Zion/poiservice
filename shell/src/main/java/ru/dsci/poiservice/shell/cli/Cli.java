package ru.dsci.poiservice.shell.cli;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.dsci.poiservice.core.services.GeoService;
import ru.dsci.poiservice.shell.services.ShellService;

import java.math.BigDecimal;

@ShellComponent
@RequiredArgsConstructor
@Slf4j
public class Cli {

    private final ShellService shellService;

    private final GeoService geoService;

    @ShellMethod(key = "lim", value = "list map items from yandex map")
    public void updatePoiFromYandexMap(
            @ShellOption(
                    value = {"-u", "--url"},
                    help = "yandex map url")
                    String url) {
        try {
            shellService.getItemsFromYandexMap(url).forEach(log::info);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    @ShellMethod(key = "upm", value = "update POIs from yandex map")
    public void updatePoiFromYandexMap(
            @ShellOption(
                    value = {"-t", "--type"},
                    help = "POI type code")
                    String poiTypeCode,
            @ShellOption(
                    value = {"-u", "--url"},
                    help = "yandex map url")
                    String url,
            @ShellOption(
                    value = {"-p", "--pref"},
                    help = "address prefix",
                    defaultValue = ShellOption.NULL)
                    String prefix) {
        try {
            shellService.updatePoisFromYandexMap(poiTypeCode, url, prefix);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    @ShellMethod(key = "cp", value = "create/update POI")
    public void updatePoi(
            @ShellOption(
                    value = {"-t", "--type"},
                    help = "POI type code",
                    defaultValue = ShellOption.NULL)
                    String typeCode,
            @ShellOption(
                    value = {"-a", "--addr"},
                    help = "POI address")
                    String address,
            @ShellOption(
                    value = {"-d", "--desc"},
                    help = "POI description")
                    String description,
            @ShellOption(
                    value = {"-g", "--geo"},
                    help = "POI GEO coordinates: latitude, longitude",
                    defaultValue = ShellOption.NULL)
                    String geo) {
        try {
            String[] geoArray = geo.split(",");
            BigDecimal geoLat = BigDecimal.valueOf(Long.parseLong(geoArray[0]));
            BigDecimal geoLon = BigDecimal.valueOf(Long.parseLong(geoArray[1]));
            log.info(shellService.updatePoi(typeCode, address, description, geoLat, geoLon).toString());
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    @ShellMethod(key = "cpo", value = "create/update POI by address using OSM service")
    public void updatePoiOsm(
            @ShellOption(
                    value = {"-c", "--code"},
                    help = "POI type code")
                    String code,
            @ShellOption(
                    value = {"-a", "--addr"},
                    help = "POI address")
                    String address,
            @ShellOption(
                    value = {"-d", "--desc"},
                    help = "description",
                    defaultValue = ShellOption.NULL)
                    String description) {
        try {
            log.info(shellService.updatePoiOsm(code, address, description).toString());
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    @ShellMethod(key = "cpt", value = "create/update POI type")
    public void createPoiType(
            @ShellOption(
                    value = {"-c", "--code"},
                    help = "POI type code")
                    String code,
            @ShellOption(
                    value = {"-t", "--title"},
                    help = "POI type title")
                    String title,
            @ShellOption(
                    value = {"-d", "--desc"},
                    help = "POI type description",
                    defaultValue = ShellOption.NULL)
                    String description) {
        try {
            log.info(shellService.updatePoiType(code, title, description).toString());
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }


    @ShellMethod(key = "lpt", value = "list POI types")
    public void listPoiTypes() {
        try {
            shellService.getPoiTypes().forEach(p -> log.info(p.toString()));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    @ShellMethod(key = "lp", value = "list POI")
    public void listPoiByType(
            @ShellOption(
                    value = {"-t", "--type"},
                    help = "POI type code")
                    String poiTypeCode
    ) {
        try {
            shellService.getPoiByType(poiTypeCode).forEach(p -> log.info(p.toString()));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    @ShellMethod(key = "gpa", value = "get POI data by address from OSM service")
    public void getPoiFromOsmByAddress(
            @ShellOption(
                    value = {"-a", "--address"},
                    help = "object address")
                    String address
    ) {
        try {
            log.info(geoService.getByAddress(address).toString());
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

}
