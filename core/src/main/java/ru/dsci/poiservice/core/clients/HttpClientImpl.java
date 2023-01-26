package ru.dsci.poiservice.core.clients;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

    public HttpResponse<String> getResponse(String url) throws IOException {
        HttpResponse<String> response;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .build();
            response = this.getClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300)
                throw new IOException(String.format("response status: %d", response.statusCode()));
        } catch (InterruptedException | IOException e) {
            throw new IOException(String.format("bad response [%s]: %s", url, e.getMessage()));
        }
        return response;
    }

}
