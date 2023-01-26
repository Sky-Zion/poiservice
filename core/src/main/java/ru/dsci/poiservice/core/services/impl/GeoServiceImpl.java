package ru.dsci.poiservice.core.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.dsci.poiservice.core.entities.dtos.DtoPoi;
import ru.dsci.poiservice.core.services.GeoService;

import javax.persistence.EntityNotFoundException;

@Primary
@Slf4j
@Service
@RequiredArgsConstructor
public class GeoServiceImpl implements GeoService {

    private final GeocoderGeoService geocoderGeoService;

    private final OsmGeoService osmGeoService;

    @Override
    public DtoPoi getByAddress(String address) throws EntityNotFoundException {
        DtoPoi dtoPoi = null;
        try {
            dtoPoi = osmGeoService.getByAddress(address);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        if (dtoPoi == null
                || dtoPoi.getGeoLat() == null
                || dtoPoi.getGeoLon() == null
                || dtoPoi.getAddress() == null
                || dtoPoi.getAddress().length() < 1)
            dtoPoi = geocoderGeoService.getByAddress(address);
        return dtoPoi;
    }
}
