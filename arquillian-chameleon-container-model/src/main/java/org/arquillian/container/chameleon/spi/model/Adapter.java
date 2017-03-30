/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.arquillian.container.chameleon.spi.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adapter {

    private String type;
    private String coordinates;
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
        deps.add(coordinates);
        if (dependencies != null) {
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
