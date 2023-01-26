package ru.dsci.poiservice.shell;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.dsci.poiservice.core.clients.HttpClientImpl;
import ru.dsci.poiservice.core.mappers.PoiMapper;
import ru.dsci.poiservice.core.repositories.PoiTypeRepository;
import ru.dsci.poiservice.core.services.PoiTypeService;
import ru.dsci.poiservice.core.services.impl.GeocoderGeoService;
import ru.dsci.poiservice.core.services.impl.PoiTypeServiceImpl;

@Configuration
@ComponentScan(basePackages = {"ru.dsci.poiservice.core"})
@EnableJpaRepositories("ru.dsci.poiservice.core.repositories")
@EntityScan({"ru.dsci.poiservice.core.entities"})
public class ShellConfig {

    @Bean
    public PoiTypeService poiTypeService(PoiTypeRepository poiTypeRepository) {
        return new PoiTypeServiceImpl(modelMapper(), poiTypeRepository);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new PoiMapper();
    }

    @Bean
    public HttpClientImpl httpClient() {
        return new HttpClientImpl();
    }

}
