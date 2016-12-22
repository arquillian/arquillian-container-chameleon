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

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URLClassLoader;

import org.arquillian.container.chameleon.controller.Resolver;
import org.arquillian.container.chameleon.spi.model.Container;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;

import static org.arquillian.container.chameleon.Utils.toMavenDependencies;
import static org.arquillian.container.chameleon.Utils.toURLs;

public class ContainerLoader {

    public Container[] load(InputStream containers, File cacheFolder, String settingsXml) throws Exception {

        MavenDependency[] mavenDependencies = toMavenDependencies(
                new String[]{"org.yaml:snakeyaml:1.15"},
                new String[]{});

        File[] archives = Resolver.resolve(cacheFolder, mavenDependencies, settingsXml);

        ClassLoader classloader = new URLClassLoader(toURLs(archives), null);
        return loadContainers(classloader, containers);
    }

    /*
    Constructor constructor = new Constructor();
    constructor.addTypeDescription(new TypeDescription(Container[].class, "tag:yaml.org,2002:" + Container[].class.getName()));

    Yaml yaml = new Yaml(constructor);
    yaml.setBeanAccess(BeanAccess.FIELD);

    return yaml.loadAs(containers, Container[].class);
     */
    private Container[] loadContainers(ClassLoader classloader, InputStream containers) throws Exception {
        Class<?> constructorClass = classloader.loadClass("org.yaml.snakeyaml.constructor.Constructor");
        Class<?> baseConstructorClass = classloader.loadClass("org.yaml.snakeyaml.constructor.BaseConstructor");

        Class<?> typeDescriptionClass = classloader.loadClass("org.yaml.snakeyaml.TypeDescription");
        Class<?> yamlClass = classloader.loadClass("org.yaml.snakeyaml.Yaml");
        Class<?> beanAccessClass = classloader.loadClass("org.yaml.snakeyaml.introspector.BeanAccess");
        Constructor<?> typeDescriptionConst = typeDescriptionClass.getConstructor(new Class[]{Class.class, String.class});

        Method addTypeDescription = constructorClass.getMethod("addTypeDescription", new Class<?>[]{typeDescriptionClass});

        Method setBeanAccess = yamlClass.getDeclaredMethod("setBeanAccess", new Class<?>[]{beanAccessClass});
        Method loadAs = yamlClass.getDeclaredMethod("loadAs", new Class<?>[]{InputStream.class, Class.class});

        Object constructor = constructorClass.newInstance();

        // Pre register type to avoid Yaml trying Class.forName on it's own classloader with our class.
        addTypeDescription.invoke(constructor, typeDescriptionConst.newInstance(Container[].class, "tag:yaml.org,2002:" + Container[].class.getName()));

        Object yaml = yamlClass.getConstructor(new Class[]{baseConstructorClass}).newInstance(new Object[]{constructor});

        Object fieldBeanAccess = null;
        for (Object beanAccess : beanAccessClass.getEnumConstants()) {
            if ("FIELD".equals(beanAccess.toString())) {
                fieldBeanAccess = beanAccess;
                break;
            }
        }

        setBeanAccess.invoke(yaml, fieldBeanAccess);

        return (Container[]) loadAs.invoke(yaml, new Object[]{containers, Container[].class});
    }
}
