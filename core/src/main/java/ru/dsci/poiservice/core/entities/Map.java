package ru.dsci.poiservice.core.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
@Table(name = "map")
public class Map {

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
        if (!(o instanceof Map)) return false;
        Map map = (Map) o;
        return getCode().equals(map.getCode()) && getTitle().equals(map.getTitle()) && getUrl().equals(map.getUrl()) && Objects.equals(getDescription(), map.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode(), getTitle(), getUrl(), getDescription());
    }

}
