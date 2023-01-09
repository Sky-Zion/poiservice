package ru.dsci.poiservice.shell.scrapers;

import com.microsoft.playwright.Playwright;

public class PlaywrightInstance {

    private final static Playwright PLAYWRIGHT = Playwright.create();

    public static Playwright getInstance() {
        return PLAYWRIGHT;
    }

}
