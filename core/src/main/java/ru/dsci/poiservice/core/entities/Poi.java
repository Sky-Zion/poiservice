package ru.dsci.poiservice.core.entities;

import ru.dsci.poiservice.core.Constants;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Data
@Table(name = "poi")
public class Poi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "osm_id")
    private Long osmId;

    @Column(name = "address")
    private String address;

    @Column(name = "description")
    private String description;

    @Column(name = "geo_lat")
    private BigDecimal geoLat;

    @Column(name = "geo_lon")
    private BigDecimal geoLon;

    @ManyToOne()
    @JoinColumn(name = "type_id")
    private PoiType poiType;

    @Override
    public String toString() {
        return String.format("#%d %s: %s [%s,%s]: %s",
                id,
                poiType.getTitle(),
                address,
                geoLat != null ? Constants.GEO_FORMAT.format(geoLat) : "",
                geoLon != null ? Constants.GEO_FORMAT.format(geoLon) : "",
                description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Poi)) return false;
        Poi poi = (Poi) o;
        return Objects.equals(getOsmId(), poi.getOsmId()) && Objects.equals(getAddress(), poi.getAddress()) && getDescription().equals(poi.getDescription()) && Objects.equals(getGeoLat(), poi.getGeoLat()) && Objects.equals(getGeoLon(), poi.getGeoLon()) && getPoiType().equals(poi.getPoiType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOsmId(), getAddress(), getDescription(), getGeoLat(), getGeoLon(), getPoiType());
    }
}
