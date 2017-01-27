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

import org.arquillian.container.chameleon.configuration.Loader;
import org.arquillian.container.chameleon.configuration.spi.model.Container;
import org.arquillian.container.chameleon.controller.Resolver;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;

import java.io.File;
import java.io.InputStream;
import java.net.URLClassLoader;

public class ContainerLoader extends Loader {

    public Container[] load(InputStream containers, File cacheFolder) throws Exception {

        MavenDependency[] mavenDependencies = Utils.toMavenDependencies(
                new String[]{"org.yaml:snakeyaml:1.17"},
                new String[]{});

        File[] archives = Resolver.resolve(cacheFolder, mavenDependencies);

        ClassLoader classloader = new URLClassLoader(Utils.toURLs(archives), null);
        return loadContainers(classloader, containers);
    }

}
