package ru.dsci.poiservice.core.services.impl;

import org.springframework.stereotype.Component;
import ru.dsci.poiservice.core.entities.PoiType;
import ru.dsci.poiservice.core.repositories.PoiTypeRepository;
import ru.dsci.poiservice.core.services.PoiTypeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Component
@RequiredArgsConstructor
public class PoiTypeServiceImpl implements PoiTypeService {

    private final ModelMapper modelMapper;
    private final PoiTypeRepository poiTypeRepository;

    @Override
    public PoiType getByCode(String typeCode) {
        return poiTypeRepository.getFirstByCodeIgnoreCase(typeCode);
    }

    @Override
    public PoiType getByCodeNotFoundError(String typeCode) {
        PoiType poiType = getByCode(typeCode);
        if (poiType == null)
            throw new EntityNotFoundException(String.format("Poi type does not exist: %s", typeCode));
        return poiType;
    }

    @Override
    public PoiType update(PoiType poiType) {
        PoiType existingPoiType;
        existingPoiType = getByCode(poiType.getCode());
        if (existingPoiType == null)
            existingPoiType = poiTypeRepository.save(poiType);
        else {
            if (poiType.equals(existingPoiType))
                return existingPoiType;
            modelMapper.map(poiType, existingPoiType);
            poiTypeRepository.save(existingPoiType);
        }
        return existingPoiType;
    }

    @Override
    public List<PoiType> getAll() {
        return poiTypeRepository.findAll();
    }

}
