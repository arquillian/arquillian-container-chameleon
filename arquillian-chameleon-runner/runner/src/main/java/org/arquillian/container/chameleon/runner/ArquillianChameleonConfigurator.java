package org.arquillian.container.chameleon.runner;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.logging.Logger;
import javax.xml.transform.TransformerException;

public class ArquillianChameleonConfigurator {

    private static final Logger log = Logger.getLogger(ArquillianChameleonConfigurator.class.getName());

    public Path setup(Class<?> testClass, ClassLoader parent) throws IOException, TransformerException {

        final InputStream configurationProperties =
            parent.getResourceAsStream(ArquillianChameleonConfigurationGenerator.ARQUILLIAN_PROPERTIES);

        ArquillianChameleonConfigurationGenerator arquillianChameleonConfigurationGenerator = new ArquillianChameleonConfigurationGenerator();

        Path arquillianChameleonConfiguration;
        if (isArquillianConfiguredWithProperties(configurationProperties)) {

            configurationProperties.close();

            final InputStream configurationXml =
                parent.getResourceAsStream(ArquillianChameleonConfigurationGenerator.ARQUILLIAN_XML);

            if (isArquillianConfiguredWithXml(configurationXml)) {

                arquillianChameleonConfiguration = arquillianChameleonConfigurationGenerator.generateAppendedArquillianXml(testClass, configurationXml);
                System.setProperty("arquillian.xml", arquillianChameleonConfiguration.getFileName().toString());

                log.warning("Current project is configured with arquillian.properties and arquillian.xml. So we have created a custom arquillian filename and set it using a JVM System property."
                    + "If you are planning to run tests in parallel in same JVM, this might cause some problems. We recommend to configure Arquillian either with .proeprties or .xml file approach but not both.");

            } else {
                arquillianChameleonConfiguration =
                    arquillianChameleonConfigurationGenerator.generateNewArquillianXml(testClass);
            }


        } else {
            arquillianChameleonConfiguration =
                arquillianChameleonConfigurationGenerator.generateNewArquillianProperties(testClass);
        }

        return arquillianChameleonConfiguration;
    }

    private boolean isArquillianConfiguredWithProperties(InputStream properties) {
        return properties != null;
    }

    private boolean isArquillianConfiguredWithXml(InputStream xml) {
        return xml != null;
    }

}
