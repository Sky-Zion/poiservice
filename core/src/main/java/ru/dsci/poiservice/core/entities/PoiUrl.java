package ru.dsci.poiservice.core.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
@Table(name = "poi_url")
public class PoiUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "title")
    private String title;

    @Column(name = "url")
    private String url;

    @Column(name = "description")
    private String description;

    @Override
    public String toString() {
        return String.format("%s: %s (%s)", code, title, description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PoiUrl)) return false;
        PoiUrl poiUrl = (PoiUrl) o;
        return getCode().equals(poiUrl.getCode()) && getTitle().equals(poiUrl.getTitle()) && getUrl().equals(poiUrl.getUrl()) && Objects.equals(getDescription(), poiUrl.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode(), getTitle(), getUrl(), getDescription());
    }

}
