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
            StringBuilder text = new StringBuilder();
            ArrayList<String> textArray = (ArrayList<String>) locator.nth(i).allInnerTexts();
            for (int j = 0; j < textArray.size(); j++) {
                text.append(text.length() < 1 ? textArray.get(j) : "\n" + textArray.get(j));
            }
            list.add(text.toString());
        }
        return list;
    }

    public ScraperList(String url, String pattern) {
        super.setUrl(url);
        this.pattern = pattern;
    }

}
