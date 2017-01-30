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
package org.arquillian.container.chameleon;

import org.jboss.arquillian.container.spi.ConfigurationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * FileUtils
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class FileUtils {

    public static InputStream loadConfiguration(String resourceName, boolean isDefault) {
        InputStream stream = loadResource(resourceName);
        if (stream == null) {
            if (isDefault) {
                throw new IllegalStateException(
                        "Could not find built-in configuration as file nor classloader resource: " + resourceName + ". " +
                                "Something is terrible wrong with the Classpath.");
            } else {
                throw new ConfigurationException(
                        "Could not locate configured containerConfigurationFile as file" +
                                " nor classloader resource: " + resourceName);
            }
        }

        return stream;
    }

    private static InputStream loadResource(String resourceName) {
        InputStream stream = loadClassPathResource(resourceName);
        if (stream == null) {
            stream = loadFileResource(resourceName);
        }
        return stream;
    }

    private static InputStream loadFileResource(String resourceName) {
        File file = new File(resourceName);
        if (file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                // should not happen unless file has been deleted since we did
                // file.exists call
                throw new IllegalStateException("Configuration file could not be found, " + resourceName);
            }
        }
        return null;
    }

    private static InputStream loadClassPathResource(String resourceName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResourceAsStream(resourceName);
    }
}