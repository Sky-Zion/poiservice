package ru.dsci.poiservice.core.entities.dtos;

import ru.dsci.poiservice.core.Constants;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DtoPoi {

    private Long geoId;

    private String address;

    private BigDecimal geoLat;

    private BigDecimal geoLon;

    @Override
    public String toString() {
        return String.format("#%d %s [%s,%s]",
                geoId,
                address,
                geoLat != null? Constants.GEO_FORMAT.format(geoLat): "",
                geoLat != null? Constants.GEO_FORMAT.format(geoLon): ""
        );
    }

}
