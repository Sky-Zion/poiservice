package ru.dsci.poiservice.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.dsci.poiservice.core.entities.PoiUrl;

@Component
@Repository
public interface PoiUrlRepository extends JpaRepository<PoiUrl, Long> {

    PoiUrl getFirstByCodeIgnoreCase(String mapCode);

}
