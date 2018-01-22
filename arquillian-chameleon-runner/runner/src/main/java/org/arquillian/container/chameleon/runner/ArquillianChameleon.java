package org.arquillian.container.chameleon.runner;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import org.arquillian.container.chameleon.runner.extension.ChameleonRunnerAppender;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

public class ArquillianChameleon extends Arquillian {

    public ArquillianChameleon(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    public void run(RunNotifier notifier) {

        Class<?> testClass = getTestClass().getJavaClass();
        final ClassLoader parent = Thread.currentThread().getContextClassLoader();

        if (isInClientSide(parent)) {
            try {
                Path arquillianChameleonConfiguration = new ArquillianChameleonConfigurator().setup(testClass, parent);
                addsArquillianFile(arquillianChameleonConfiguration.getParent(),
                    parent);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        super.run(notifier);
    }

    private boolean isInClientSide(ClassLoader parent) {
        return parent.getResourceAsStream(ChameleonRunnerAppender.CHAMELEON_RUNNER_INCONTAINER_FILE) == null;
    }

    private void addsArquillianFile(Path file, ClassLoader classLoader) throws Exception {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] {URL.class});
        method.setAccessible(true);
        method.invoke(classLoader, new Object[] {file.toUri().toURL()});
    }

}
