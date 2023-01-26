package ru.dsci.poiservice.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.dsci.poiservice.core.entities.Poi;
import ru.dsci.poiservice.core.entities.PoiType;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PoiRepository extends JpaRepository<Poi, Long> {

    List<Poi> getAllByPoiType(PoiType poiType);

    List<Poi> getAllByPoiTypeAndGeoLatIsBetweenAndGeoLonIsBetween(
            PoiType poiType,
            BigDecimal begLat,
            BigDecimal endLat,
            BigDecimal begLong,
            BigDecimal endLong);

    Poi getByPoiTypeAndOsmId(PoiType poiType, Long osmId);

    Poi getByPoiTypeAndAddressIgnoreCase(PoiType poiType, String address);

    Poi getByPoiTypeAndDescriptionIgnoreCase(PoiType poiType, String description);

}
