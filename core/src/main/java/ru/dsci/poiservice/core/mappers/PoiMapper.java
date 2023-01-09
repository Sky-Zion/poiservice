package ru.dsci.poiservice.core.mappers;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;
import ru.dsci.poiservice.core.entities.Poi;
import ru.dsci.poiservice.core.entities.dtos.DtoOsmPoi;

import javax.annotation.PostConstruct;

@Component
public class PoiMapper extends ModelMapper {

    @PostConstruct
    private void init() {
        this.createTypeMap(DtoOsmPoi.class, Poi.class).setConverter(mappingContext -> {
            DtoOsmPoi dtoOsmPoi = mappingContext.getSource();
            Poi poi = mappingContext.getDestination();
            if (poi == null) poi = new Poi();
            if (dtoOsmPoi != null) {
                if (dtoOsmPoi.getGeoId() != null)
                    poi.setOsmId(dtoOsmPoi.getGeoId());
                if (dtoOsmPoi.getAddress() != null)
                    poi.setAddress(dtoOsmPoi.getAddress());
                if (dtoOsmPoi.getGeoLat() != null && dtoOsmPoi.getGeoLon() != null) {
                    poi.setGeoLat(dtoOsmPoi.getGeoLat());
                    poi.setGeoLon(dtoOsmPoi.getGeoLon());
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
