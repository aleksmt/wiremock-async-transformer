package ru.github.mrcerbycsy.wiremock.internals;

import com.github.tomakehurst.wiremock.extension.Parameters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArgumentChecker {

    /**
     * Logger for the current class
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    private Parameters params;

    /**
     * First, check everything that might be necessary, saves it for the later usage
     * @param parameters from the wiremock plugin
     */
    public ArgumentChecker(Parameters parameters) {
        params = parameters;
        if (params == null || params.isEmpty())
            throw new RuntimeException("Argument params cannot be empty");
        log.info("Parameters passed: ");
        params.forEach((x, y) -> log.info(x + " -> " + y));
    }

    @SuppressWarnings("unchecked")
    public <T> T check(String argument, Class<T> clazz, Object defaultValue) {
        if (defaultValue == null) {
            if (!params.containsKey(argument))
                throw new RuntimeException(argument + " argument cannot be empty");
            if (!clazz.isInstance(params.get(argument)))
                throw new RuntimeException(argument + " argument should be type of: " + clazz.getName());
        }
        T value = (T) params.getOrDefault(argument, defaultValue);
        if (value == null || value.equals("") || value.equals(0)) {
            throw new RuntimeException(argument + " argument value cannot be null, empty or zero");
        }
        return value;
    }
}
