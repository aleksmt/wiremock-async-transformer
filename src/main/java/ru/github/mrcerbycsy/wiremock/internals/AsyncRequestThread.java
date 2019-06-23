package ru.github.mrcerbycsy.wiremock.internals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AsyncRequestThread extends Thread {

    /**
     * Logger for the current class
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Shared state between the transformer and async thread
     */
    public static volatile boolean interrupted = false;

    /**
     * Apache Http client that should be used within a new thread
     */
    private CloseableHttpClient client;

    /**
     * Request builder for the Http client
     */
    private RequestBuilder request;

    /**
     * [Required] HTTP address where to send request
     */
    private URL url;

    /**
     * Request payload as a string if necessary. Not applicable when GET or similar method passed
     */
    private String body;

    /**
     * Store for the key-value pairs of headers
     */
    private Map<String, String> headers;

    /**
     * How long (in milliseconds) should wait newly created thread before send async request, default = 5 sec
     */
    private int delay = 5000;

    /**
     * General constructor, prepares http-client, performs request after delay
     * @param url formed outside, protocol, host, port and path must be included here
     * @param method one of the: GET, POST, PUT, etc.
     * @param body string representation of the request body
     * @param delay milliseconds that should be passed between thread invocation via .start() and an http request
     */
    public AsyncRequestThread(@NotNull URL url, @NotNull String method, Map<String, String> headers, String body, int delay) {
        this.delay = delay < 0 ? this.delay : delay;
        this.body = body;
        this.url = url;
        this.headers = headers;
        this.client = HttpClientBuilder.create().build();
        this.request = RequestBuilder.create(method.toUpperCase());
    }

    public void run() {
        log.info("Gathering url, delay, and all the stuff together. Sleeping for: " + delay + " seconds");
        try {
            request.setUri(url.toURI());
            if ((body != null) && !body.isEmpty()) {
                request.setEntity(new StringEntity(body, Charset.forName("UTF-8")));
            }
            if ((headers != null) && !headers.isEmpty()) {
                headers.forEach((k, v) -> request.addHeader(k, v));
            }
            Thread.sleep(delay);
            CloseableHttpResponse response = client.execute(request.build());
            log.info("Response received. Status: " + response.getStatusLine().getStatusCode());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
