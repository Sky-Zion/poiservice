package ru.dsci.poiservice.core.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.dsci.poiservice.core.entities.dtos.DtoPoi;

import javax.persistence.EntityNotFoundException;

@Primary
@Slf4j
@Service
@RequiredArgsConstructor
public class GeoServiceImplImpl implements ru.dsci.poiservice.core.services.GeoServiceImpl {

    private final GeocoderGeoServiceImpl geocoderGeoService;

    private final OsmGeoServiceImpl osmGeoService;

    @Override
    public DtoPoi getByAddress(String address) throws EntityNotFoundException {
        DtoPoi dtoPoi = null;
        try {
            dtoPoi = osmGeoService.getByAddress(address);
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
        if (dtoPoi == null
                || dtoPoi.getGeoLat() == null
                || dtoPoi.getGeoLon() == null
                || dtoPoi.getAddress() == null
                || dtoPoi.getAddress().length() < 1)
            try {
                dtoPoi = geocoderGeoService.getByAddress(address);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        return dtoPoi;
    }
}
