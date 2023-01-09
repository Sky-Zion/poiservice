package ru.dsci.poiservice.shell.scrapers.exceptions;

import ru.dsci.poiservice.shell.scrapers.Scraper;

public class ScrapingExecutionException extends RuntimeException {

    public ScrapingExecutionException(Scraper<?> scraper, String message) {
        this(scraper, message, null);
    }

    public ScrapingExecutionException(Scraper<?> scraper, String message, Exception e) {
        super(String.format("Scraper '%s' execution error (execution time: %d/%d ms): %s", scraper.getClass().getSimpleName(), System.currentTimeMillis() - scraper.getBegTime(), scraper.getTimeout(), message), e);
    }

}
