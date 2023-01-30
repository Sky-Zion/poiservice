package ru.dsci.poiservice.core.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.dsci.poiservice.core.entities.Map;
import ru.dsci.poiservice.core.repositories.MapRepository;
import ru.dsci.poiservice.core.services.MapService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MapServiceImpl implements MapService {

    private final MapRepository mapRepository;

    @Override
    public Map getByCode(String mapCode) {
        return mapRepository.getFirstByCodeIgnoreCase(mapCode);
    }

    @Override
    public List<Map> getAll() {
        return mapRepository.findAll();
    }

}
