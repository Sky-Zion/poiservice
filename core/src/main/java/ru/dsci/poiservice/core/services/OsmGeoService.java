package ru.dsci.poiservice.core.services;

import ru.dsci.poiservice.core.entities.dtos.DtoOsmPoi;

import javax.persistence.EntityNotFoundException;

public interface OsmGeoService {

    DtoOsmPoi getByAddress(String address) throws EntityNotFoundException;

}
