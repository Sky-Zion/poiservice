package ru.dsci.poiservice.shell.scrapers.exceptions;

import ru.dsci.poiservice.shell.scrapers.Scraper;

public class ScrapingException extends RuntimeException {

    public ScrapingException(Scraper<?> scraper, String message) {
        this(scraper, message, null);
    }

    public ScrapingException(Scraper<?> scraper, String message, Exception e) {
        super(String.format("Page '%s' can't be scraped: %s",
                scraper.getUrl(), message));
    }

}
