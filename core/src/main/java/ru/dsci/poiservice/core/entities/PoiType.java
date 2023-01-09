package ru.dsci.poiservice.core.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
@Table(name = "poi_type")
public class PoiType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Override
    public String toString() {
        return String.format("%s: %s (%s)", code, title, description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PoiType)) return false;
        PoiType poiType = (PoiType) o;
        return getCode().equals(poiType.getCode()) && getTitle().equals(poiType.getTitle()) && Objects.equals(getDescription(), poiType.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode(), getTitle(), getDescription());
    }
}
