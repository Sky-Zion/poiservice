package ru.dsci.poiservice.shell.scrapers;

import com.microsoft.playwright.Locator;
import ru.dsci.poiservice.shell.scrapers.exceptions.ScrapingException;

import java.util.ArrayList;
import java.util.List;

public class ScraperList extends Scraper<String> {

    private final String pattern;

    @Override
    protected List<String> parse() {
        List<String> list = new ArrayList<>();
        Locator locator = super.page.locator(pattern);
        if (locator.count() == 0) throw new ScrapingException(this, "No data");
        for (int i = 0; i < locator.count(); i++) {
            list.add(locator.nth(i).innerText());
        }
        return list;
    }

    public ScraperList(String url, String pattern) {
        super.setUrl(url);
        this.pattern = pattern;
    }

}
