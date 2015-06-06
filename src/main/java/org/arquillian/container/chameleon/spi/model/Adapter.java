package org.arquillian.container.chameleon.spi.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adapter {

    private String type;
    private String gav;
    private String adapterClass;
    private boolean requireDist = true;
    private String[] dependencies;
    private Map<String, String> configuration;

    public boolean isType(Target.Type targetType) {
        return targetType.name().toLowerCase().equalsIgnoreCase(type);
    }

    public String adapterClass() {
        return adapterClass;
    }

    public boolean requireDist() {
        return requireDist;
    }

    public String[] dependencies() {
        List<String> deps = new ArrayList<String>();
        deps.add(gav);
        if(dependencies != null) {
            deps.addAll(Arrays.asList(dependencies));
        }
        return deps.toArray(new String[] {});
    }

    public Map<String, String> configuration() {
        if (configuration != null) {
            return new HashMap<String, String>(configuration);
        }
        return Collections.emptyMap();
    }
}
