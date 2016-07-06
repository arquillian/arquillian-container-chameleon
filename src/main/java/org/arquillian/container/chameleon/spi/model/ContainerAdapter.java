package org.arquillian.container.chameleon.spi.model;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContainerAdapter {

    private String version;
    private Target.Type targetType;
    private Adapter adapter;
    private Dist dist;
    private String[] gavExcludeExpression;
    private String defaultProtocol;

    public ContainerAdapter(String version, Target.Type targetType, Adapter adapter, Dist dist,
                            String[] gavExcludeExpression, String defaultProtocol) {
        this.version = version;
        this.targetType = targetType;
        this.adapter = adapter;
        this.dist = dist;
        this.gavExcludeExpression = gavExcludeExpression;
        this.defaultProtocol = defaultProtocol;
    }

    public Target.Type type() {
        return targetType;
    }

    public boolean overrideDefaultProtocol() {
        return this.defaultProtocol != null;
    }

    public String getDefaultProtocol() {
        return defaultProtocol;
    }

    public String adapterClass() {
        return adapter.adapterClass();
    }

    public String[] dependencies() {
        return resolve(adapter.dependencies());
    }

    public String distribution() {
        return resolve(dist.gav());
    }

    public String[] excludes() {
        return resolve(gavExcludeExpression);
    }

    public String[] configurationKeys() {
        return adapter.configuration().keySet().toArray(new String[]{});
    }

    public boolean requireDistribution() {
        return adapter.requireDist();
    }

    public Map<String, String> resolveConfiguration(Map<String, String> parameters) {
        Map<String, String> configuration = adapter.configuration();
        for (Map.Entry<String, String> entry : configuration.entrySet()) {
            for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                entry.setValue(resolve(parameter.getKey(), parameter.getValue(), entry.getValue()));
            }
        }
        return configuration;
    }

    private String[] resolve(String[] values) {
        if (values == null) {
            return new String[0];
        }
        String[] resolved = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            resolved[i] = resolve(values[i]);
        }
        return resolved;
    }

    private String resolve(String value) {
        return resolve("version", version, value);
    }

    private String resolve(String parameter, String value, String target) {
        return target.replaceAll(Pattern.quote("${" + parameter + "}"), Matcher.quoteReplacement(value));
    }
}
