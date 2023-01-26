package ru.dsci.poiservice.core.mappers;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;
import ru.dsci.poiservice.core.entities.Poi;
import ru.dsci.poiservice.core.entities.dtos.DtoPoi;

import javax.annotation.PostConstruct;

@Component
public class PoiMapper extends ModelMapper {

    @PostConstruct
    private void init() {
        this.createTypeMap(DtoPoi.class, Poi.class).setConverter(mappingContext -> {
            DtoPoi dtoPoi = mappingContext.getSource();
            Poi poi = mappingContext.getDestination();
            if (poi == null) poi = new Poi();
            if (dtoPoi != null) {
                if (dtoPoi.getGeoId() != null)
                    poi.setOsmId(dtoPoi.getGeoId());
                if (dtoPoi.getAddress() != null)
                    poi.setAddress(dtoPoi.getAddress());
                if (dtoPoi.getGeoLat() != null && dtoPoi.getGeoLon() != null) {
                    poi.setGeoLat(dtoPoi.getGeoLat());
                    poi.setGeoLon(dtoPoi.getGeoLon());
                }
            }
            return poi;
        });

        addMappings(new PropertyMap<>(Poi.class, Poi.class) {
            @Override
            protected void configure() {
                skip().setId(null);
            }
        });

    }

}
