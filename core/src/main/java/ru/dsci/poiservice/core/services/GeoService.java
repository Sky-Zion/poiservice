package ru.dsci.poiservice.core.services;

import ru.dsci.poiservice.core.entities.dtos.DtoPoi;

import javax.persistence.EntityNotFoundException;

public interface GeoService {

    DtoPoi getByAddress(String address) throws EntityNotFoundException;

}
