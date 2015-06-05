package org.arquillian.container.chameleon;

import static org.arquillian.container.chameleon.Utils.toMavenDependencies;
import static org.arquillian.container.chameleon.Utils.toURLs;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URLClassLoader;

import org.arquillian.container.chameleon.spi.model.Container;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;

public class ServerLoader {

    public Container[] load(String resource) throws Exception {
        InputStream containers = ServerLoader.class.getResourceAsStream(resource);

        MavenDependency[] mavenDependencies = toMavenDependencies(
                new String[] { "org.yaml:snakeyaml:1.14" },
                new String[] {});

        File[] archives = Maven.configureResolver()
                .addDependencies(mavenDependencies)
                .resolve()
                .withTransitivity()
                .asFile();

        @SuppressWarnings("resource")
        ClassLoader classloader = new URLClassLoader(toURLs(archives), ServerLoader.class.getClassLoader());

        Class<?> yamlClass = classloader.loadClass("org.yaml.snakeyaml.Yaml");
        Class<?> beanAccessClass = classloader.loadClass("org.yaml.snakeyaml.introspector.BeanAccess");

        Object yaml = yamlClass.newInstance();

        Object fieldBeanAccess = null;
        for(Object beanAccess : beanAccessClass.getEnumConstants()) {
            if ("FIELD".equals(beanAccess.toString())) {
                fieldBeanAccess = beanAccess;
                break;
            }
        }

        Method setBeanAccess = yamlClass.getDeclaredMethod("setBeanAccess", new Class<?>[]{beanAccessClass});
        setBeanAccess.invoke(yaml, fieldBeanAccess);

        Method loadAs = yamlClass.getDeclaredMethod("loadAs", new Class<?>[] {InputStream.class, Class.class});

        return (Container[]) loadAs.invoke(yaml, new Object[] {containers, Container[].class});
    }
}
