package ru.dsci.poiservice.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.dsci.poiservice.core.entities.Map;

@Component
@Repository
public interface MapRepository extends JpaRepository<Map, Long> {

    Map getFirstByCodeIgnoreCase(String mapCode);

}
