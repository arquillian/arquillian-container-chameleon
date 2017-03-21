package org.arquillian.container.chameleon;

import org.arquillian.container.chameleon.spi.model.Container;
import org.arquillian.container.chameleon.spi.model.Target;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Loader {

    /*
   Constructor constructor = new Constructor();
   constructor.addTypeDescription(new TypeDescription(Container[].class, "tag:yaml.org,2002:" + Container[].class.getName()));

   Yaml yaml = new Yaml(constructor);
   yaml.setBeanAccess(BeanAccess.FIELD);

   return yaml.loadAs(containers, Container[].class);
    */
    protected Container[] loadContainers(ClassLoader classloader, InputStream containers) throws Exception {
        Class<?> constructorClass = classloader.loadClass("org.yaml.snakeyaml.constructor.Constructor");
        Class<?> baseConstructorClass = classloader.loadClass("org.yaml.snakeyaml.constructor.BaseConstructor");

        Class<?> typeDescriptionClass = classloader.loadClass("org.yaml.snakeyaml.TypeDescription");
        Class<?> yamlClass = classloader.loadClass("org.yaml.snakeyaml.Yaml");
        Class<?> beanAccessClass = classloader.loadClass("org.yaml.snakeyaml.introspector.BeanAccess");
        Constructor<?> typeDescriptionConst =
            typeDescriptionClass.getConstructor(new Class[]{Class.class, String.class});

        Method addTypeDescription =
            constructorClass.getMethod("addTypeDescription", new Class<?>[]{typeDescriptionClass});

        Method setBeanAccess = yamlClass.getDeclaredMethod("setBeanAccess", new Class<?>[]{beanAccessClass});
        Method loadAs = yamlClass.getDeclaredMethod("loadAs", new Class<?>[]{InputStream.class, Class.class});

        Object constructor = constructorClass.newInstance();

        // Pre register type to avoid Yaml trying Class.forName on it's own classloader with our class.
        addTypeDescription.invoke(constructor,
            typeDescriptionConst.newInstance(Container[].class, "tag:yaml.org,2002:" + Container[].class.getName()));

        Object yaml =
            yamlClass.getConstructor(new Class[]{baseConstructorClass}).newInstance(new Object[]{constructor});

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

    public Container[] loadContainers(InputStream inputStream) throws Exception {
        return this.loadContainers(Target.class.getClassLoader(), inputStream);
    }
}
