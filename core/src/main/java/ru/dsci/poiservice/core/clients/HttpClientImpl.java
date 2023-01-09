package ru.dsci.poiservice.core.clients;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.http.HttpClient;
import java.time.Duration;

@Component
public class HttpClientImpl {

    private final static int CONNECT_TIMEOUT = 10;

    private HttpClient client;

    public HttpClient getClient() {
        return client;
    }

    @PostConstruct
    private void init() {
        client =
        java.net.http.HttpClient.newBuilder()
                .version(java.net.http.HttpClient.Version.HTTP_2)
                .followRedirects(java.net.http.HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT))
                .build();
    }

}
