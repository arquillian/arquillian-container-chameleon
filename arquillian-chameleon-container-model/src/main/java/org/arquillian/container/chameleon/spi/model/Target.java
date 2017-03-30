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

import org.arquillian.container.chameleon.FileUtils;
import org.arquillian.container.chameleon.Loader;
import org.jboss.arquillian.container.spi.ConfigurationException;

public class Target {

    public static enum Type

    {
        Remote, Managed, Embedded, Default;

    public static Type from(String name) {
        for (Type type : Type.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

}

    private String server;
    private String version;
    private Type type;

    public Type getType() {
        return type;
    }

    public String getServer() {
        return server;
    }

    public String getVersion() {
        return version;
    }

    public static Target from(String source) {
        Target target = new Target();

        String[] sections = source.split(":");
        if (sections.length < 2 || sections.length > 3) {
            throw new ConfigurationException("Wrong target format [" + source + "] server:version:type");
        }
        target.server = sections[0].toLowerCase();
        target.version = sections[1];
        if (sections.length > 2) {
            for (Type type : Type.values()) {
                if (sections[2].toLowerCase().contains(type.name().toLowerCase())) {
                    target.type = type;
                    break;
                }
            }
            if (target.type == null) {
                throw new ConfigurationException(
                    "Unknown target type " + sections[2] + ". Supported " + Target.Type.values());
            }
        } else {
            target.type = Type.Default;
        }
        return target;
    }

    @Override
    public String toString() {
        return server + ":" + version + ":" + type;
    }

    public boolean isSupported() throws Exception {
        Loader loader = new Loader();
        Container[] containers =
            loader.loadContainers(FileUtils.loadConfiguration("chameleon/default/containers.yaml", true));

        for (Container container : containers) {
            if (container.matches(this) != null) {
                return true;
            }
        }
        return false;
    }
}
