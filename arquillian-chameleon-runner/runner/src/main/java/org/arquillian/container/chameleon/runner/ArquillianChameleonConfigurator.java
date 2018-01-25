package org.arquillian.container.chameleon.runner;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.logging.Logger;
import javax.xml.transform.TransformerException;

public class ArquillianChameleonConfigurator {

    private static final Logger log = Logger.getLogger(ArquillianChameleonConfigurator.class.getName());
    public static final String ARQUILLIAN_XML_SYS_PROPERTY = "arquillian.xml";

    public Path setup(Class<?> testClass, ClassLoader parent) throws IOException, TransformerException {

        final InputStream configurationProperties =
            parent.getResourceAsStream(ArquillianChameleonConfigurationGenerator.ARQUILLIAN_PROPERTIES);

        ArquillianChameleonConfigurationGenerator arquillianChameleonConfigurationGenerator = new ArquillianChameleonConfigurationGenerator();

        Path arquillianChameleonConfiguration;
        if (isArquillianConfiguredWithProperties(configurationProperties)) {

            configurationProperties.close();

            final InputStream configurationXml = getArquillianXmlConfiguration(parent);

            if (isArquillianConfiguredWithXml(configurationXml)) {

                arquillianChameleonConfiguration = arquillianChameleonConfigurationGenerator.generateAppendedArquillianXml(testClass, configurationXml);
                System.setProperty(ARQUILLIAN_XML_SYS_PROPERTY, arquillianChameleonConfiguration.getFileName().toString());

                log.warning("Current project is configured with arquillian.properties and arquillian.xml. So we have created a custom arquillian filename and set it using a JVM System property."
                    + " If you are planning to run tests in parallel in same JVM, this might cause some problems. We recommend to configure Arquillian either with .proeprties or .xml file approach but not both.");

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

    private InputStream getArquillianXmlConfiguration(ClassLoader parent) {

        final String customConfigurationXml = System.getProperty(ARQUILLIAN_XML_SYS_PROPERTY);

        final InputStream customXml = parent.getResourceAsStream(customConfigurationXml);

        if (customXml != null) {
            return customXml;
        }

        final InputStream configurationXml =
            parent.getResourceAsStream(ArquillianChameleonConfigurationGenerator.ARQUILLIAN_XML);

        if (configurationXml != null) {
            return configurationXml;
        }

        return null;

    }

    private boolean isArquillianConfiguredWithProperties(InputStream properties) {
        return properties != null;
    }

    private boolean isArquillianConfiguredWithXml(InputStream xml) {
        return xml != null;
    }

}
