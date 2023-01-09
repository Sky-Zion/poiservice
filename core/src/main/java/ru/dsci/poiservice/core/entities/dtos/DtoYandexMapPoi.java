package ru.dsci.poiservice.core.entities.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DtoYandexMapPoi {

    private String address;

    private String description;

    private BigDecimal geoLat;

}
