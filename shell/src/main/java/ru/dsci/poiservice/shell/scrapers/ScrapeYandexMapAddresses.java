package ru.dsci.poiservice.shell.scrapers;

import org.springframework.stereotype.Component;


@Component
public class ScrapeYandexMapAddresses extends ScraperList {

    public static final String DEFAULT_URL = "https://yandex.ru/maps/4/belgorod/?ll=36.675005%2C50.584049&mode=usermaps&source=constructorLink&um=constructor%3A235fb866f1f5245c11290cb8e1e22527dadcee2acc5414a4cc6dcd30d732de68&z=16";
    public static final String ADDRESS_PATTERN = "div.user-maps-features-view__description";

    public ScrapeYandexMapAddresses() {
        super(DEFAULT_URL, ADDRESS_PATTERN);
    }

    @Override
    public void setUrl(String url) {
        super.setUrl(url == null ? DEFAULT_URL : url);
    }

}
