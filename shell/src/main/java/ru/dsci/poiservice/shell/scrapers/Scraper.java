package ru.dsci.poiservice.shell.scrapers;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitUntilState;
import ru.dsci.poiservice.shell.scrapers.exceptions.ScrapingExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.dsci.poiservice.shell.scrapers.exceptions.ScrapingException;

import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Component
public abstract class Scraper<E> implements AutoCloseable {

    class ScraperTask implements Callable<List<E>> {
        @Override
        public List<E> call() {
            return parse();
        }
    }

    private static final int DEFAULT_SLOW_MO = 10;
    private static final int DEFAULT_TIMEOUT = 30000;
    private static final int WAIT_FOR_SELECTOR_TIMEOUT = 15000;

    protected Playwright playwright = PlaywrightInstance.getInstance();
    protected Page page;
    protected String url;
    protected BrowserType browserType;

    private Browser browser;
    private BrowserType.LaunchOptions launchOptions;
    private int timout = DEFAULT_TIMEOUT;

    public long begTime = 0;

    public int getTimeout() {
        return timout;
    }

    public long getBegTime() {
        return begTime;
    }

    public void setBegTime(long begTime) {
        this.begTime = begTime;
    }

    public void setTimeout(int timeout) {
        if (this.launchOptions == null) launchOptions = new BrowserType.LaunchOptions();
        this.timout = timeout;
        this.launchOptions.setTimeout(WAIT_FOR_SELECTOR_TIMEOUT);
        initScraper();
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    protected void initScraper() {
        initScraper(null);
    }

    protected void initScraper(BrowserType.LaunchOptions launchOptions) {
        if (launchOptions == null) {
            launchOptions = new BrowserType.LaunchOptions();
            launchOptions.setTimeout(getTimeout());
        }
        if (playwright == null) playwright = Playwright.create();
        if (browser == null || !browser.isConnected()) {
            browserType = playwright.chromium();
            this.launchOptions = launchOptions;
            this.browser = browserType.launch(launchOptions);
        }
        if (page == null || page.isClosed()) {
            page = browser.newPage();
            page.setDefaultTimeout(WAIT_FOR_SELECTOR_TIMEOUT);
        }
    }

    protected void initDebug() {
        initScraper(launchOptions.setSlowMo(DEFAULT_SLOW_MO).setHeadless(false));
    }

    protected abstract List<E> parse() throws ScrapingException;

    public void navigate(String url) {
        try {
            page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
        } catch (RuntimeException e) {
            throw new ScrapingException(this, String.format("Resource is not available: %s: %s", url, e.getMessage()));
        }
    }

    public void navigate() {
        navigate(getUrl());
    }

    public List<E> doScrape() {
        initSettings();
        List<E> results;
        String scraperClass = this.getClass().getSimpleName();
        if (playwright == null || browser == null || !browser.isConnected() || page == null || page.isClosed()) initScraper();
        navigate();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            log.debug("Scraper '{}' started: time limit: {} ms, url: {}", scraperClass, getTimeout(), getUrl());
            if (launchOptions.headless == null || launchOptions.headless) {
                Future<List<E>> future = executor.submit(new ScraperTask());
                try {
                    setBegTime(System.currentTimeMillis());
                    results = future.get(getTimeout(), TimeUnit.MILLISECONDS);
                    log.debug("Scraper '{}' executed successfully: execution time: {}/{} ms", scraperClass, System.currentTimeMillis() - begTime, timout);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    future.cancel(true);
                    String message = e.getMessage();
                    if (message == null || message.isEmpty()) switch (e.getClass().getSimpleName()) {
                        case "InterruptedException":
                            message = "Thread interrupted";
                            break;
                        case "ExecutionException":
                            message = "Execution error";
                            break;
                        case "TimeoutException":
                            message = "Execution timeout";
                            break;
                    }
                    throw new ScrapingException(this, String.format("Scraper execution interrupted: %s", message), e);
                }
            } else
                results = parse();
        } catch (RuntimeException e) {
            throw new ScrapingExecutionException(this, String.format("Scraper stopped unexpectedly: %s", e.getMessage()), e);
        } finally {
            executor.shutdownNow();
            close();
        }
        log.debug("Scraped: {} item(s)", results != null ? results.size() : 0);
        return results;
    }

    protected void reset() {
        setUrl(null);
    }

    @Override
    public void close() {
        reset();
        page.close();
        browser.close();
    }

    protected void initSettings() {
    }

}
