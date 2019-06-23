package ru.github.mrcerbycsy.wiremock;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

import ru.github.mrcerbycsy.wiremock.internals.ArgumentChecker;
import ru.github.mrcerbycsy.wiremock.internals.AsyncRequestThread;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncRequestTransformer extends ResponseDefinitionTransformer {

    /**
     * Logger for the current transformer class
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Name of the plugin (transformer)
     */
    private static final String TRANSFORMER_NAME = "async-request";

    /**
     * Activated locally (should be used within mapping)
     */
    private static final boolean APPLY_GLOBALLY = false;

    // File read example:
    // TextFile text = fileSource.getTextFileNamed("confirmation.xml")
    // String string = text.readContentsAsString()

    @Override
    public ResponseDefinition transform(Request request,
                                        ResponseDefinition responseDefinition,
                                        FileSource fileSource,
                                        Parameters parameters) {
        ArgumentChecker c = new ArgumentChecker(parameters);

        try {
            log.info("Checking all required arguments...");
            URL url = new URL(c.check("url", String.class, null));
            String body = c.check("body", String.class, request.getBodyAsString());
            String method = c.check("method", String.class, null);
            int delay = c.check("delay", Integer.class, 5000);

            // Optional headers parameter
            HashMap<String, String> headers = new HashMap<>();
            if (parameters.containsKey("headers")) {
                parameters.getMetadata("headers").forEach((k, v) -> headers.put(k, (String) v));
            }

            // Starting in the separate thread
            new AsyncRequestThread(url, method, headers, body, delay).start();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return responseDefinition;
    }

    @Override
    public String getName() {
        return TRANSFORMER_NAME;
    }

    @Override
    public boolean applyGlobally() {
        return APPLY_GLOBALLY;
    }

}
