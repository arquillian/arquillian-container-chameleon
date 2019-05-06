package org.arquillian.container.chameleon.runner;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import org.arquillian.container.chameleon.runner.extension.ChameleonRunnerAppender;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

public class ArquillianChameleon extends Arquillian {

    private static final Logger log = Logger.getLogger(ArquillianChameleon.class.getName());

    public ArquillianChameleon(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    public void run(RunNotifier notifier) {

        final Class<?> testClass = getTestClass().getJavaClass();
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        synchronized (ArquillianChameleon.class) {
            if (isInClientSide(classLoader) && !isSpecialChameleonFile(classLoader)) {

                try {
                    final Path arquillianChameleonConfiguration =
                        new ArquillianChameleonConfigurator().setup(testClass, classLoader);

                    log.info(String.format("Arquillian Configuration created by Chameleon runner is placed at %s.",
                        arquillianChameleonConfiguration.toFile().getAbsolutePath()));

                    createChameleonMarkerFile(arquillianChameleonConfiguration.getParent());
                    addsArquillianFile(arquillianChameleonConfiguration.getParent(),
                        classLoader);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
        super.run(notifier);
    }


     // We need to create an special file and put it inside classloader. This is because Chameleon runner adds configuration files dynamically inside the classpath.
     // When you run your tests from your build tool, some or all of them shares the same classloader. So we need to avoid calling the same logic all the time to not get multiple files placed at classloader
     // representing different versions, because then you have uncertainty on which configuration file is really used
    private void createChameleonMarkerFile(Path parent) throws IOException {
        final Path chameleon = parent.resolve("chameleonrunner");
        Files.write(chameleon, "Chameleon Runner was there".getBytes());
    }

    private boolean isSpecialChameleonFile(ClassLoader parent) {
        return parent.getResource("chameleonrunner") != null;
    }

    private boolean isInClientSide(ClassLoader parent) {
        return parent.getResourceAsStream(ChameleonRunnerAppender.CHAMELEON_RUNNER_INCONTAINER_FILE) == null;
    }

    private void addsArquillianFile(Path file, ClassLoader classLoader) throws Exception {
        final URL url = file.toUri().toURL();
        final ClassLoader wrappedClassLoader = URLClassLoader.newInstance(new URL[]{url}, classLoader);
        Thread.currentThread().setContextClassLoader(wrappedClassLoader);
    }

}
