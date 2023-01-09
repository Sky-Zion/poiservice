package ru.dsci.poiservice.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.dsci.poiservice.core.entities.PoiType;

@Repository
public interface PoiTypeRepository extends JpaRepository<PoiType, Long> {
    PoiType getFirstByCodeIgnoreCase(String code);
}
