package ru.dsci.poiservice.core.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.dsci.poiservice.core.entities.PoiUrl;
import ru.dsci.poiservice.core.repositories.PoiUrlRepository;
import ru.dsci.poiservice.core.services.PoiUrlService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PoiUrlServiceImpl implements PoiUrlService {

    private final PoiUrlRepository poiUrlRepository;

    @Override
    public PoiUrl getByCode(String mapCode) {
        return poiUrlRepository.getFirstByCodeIgnoreCase(mapCode);
    }

    @Override
    public List<PoiUrl> getAll() {
        return poiUrlRepository.findAll(Sort.by(Sort.Direction.ASC, "title"));
    }

}
