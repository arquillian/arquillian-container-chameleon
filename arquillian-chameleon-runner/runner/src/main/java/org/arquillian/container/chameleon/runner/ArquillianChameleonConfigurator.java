package org.arquillian.container.chameleon.runner;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import javax.xml.transform.TransformerException;

public class ArquillianChameleonConfigurator {

    

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
