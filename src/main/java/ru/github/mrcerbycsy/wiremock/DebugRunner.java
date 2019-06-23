package ru.github.mrcerbycsy.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import com.opentable.extension.BodyTransformer;

public class DebugRunner {

    public static void main(String[] args) {
        WireMockServer server = new WireMockServer(
            options()
                .extensions(new AsyncRequestTransformer())
                .extensions(new BodyTransformer())
                .port(8080));
        server.start();
    }

}
