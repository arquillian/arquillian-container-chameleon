package org.arquillian.container.chameleon.runner;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ArquillianChameleonConfigurationGenerator {

    static final String ARQUILLIAN_CHAMELEON_XML = "arquillianchameleon.xml";
    static final String ARQUILLIAN_XML = "arquillian.xml";
    static final String ARQUILLIAN_PROPERTIES = "arquillian.properties";

    public Path generateNewArquillianProperties(Class<?> testClass) throws IOException {
        final ChameleonTargetConfiguration chameleonTargetConfiguration = findChameleonTargetConfiguration(testClass);

        if (chameleonTargetConfiguration != null) {

            final Properties containerDefinitionAsProperties =
                chameleonTargetConfiguration.getContainerDefinitionAsProperties();

            final Path tempDirectory = Files.createTempDirectory("chameleonContainer");
            final Path arquillianPath = tempDirectory.resolve(ARQUILLIAN_PROPERTIES);

            containerDefinitionAsProperties.store(new FileWriter(arquillianPath.toFile()), "");

            return arquillianPath;

        }

        return null;
    }

    public Path generateNewArquillianXml(Class<?> testClass)
        throws IOException, TransformerException {
        final ChameleonTargetConfiguration chameleonTargetConfiguration = findChameleonTargetConfiguration(testClass);

        if (chameleonTargetConfiguration != null) {

            final Node containerDefinition = chameleonTargetConfiguration.getContainerDefinitionAsXml();
            Document arquillianConfigurationDocument = DomManipulation.createDocumentFromTemplate();

            return generate(arquillianConfigurationDocument, containerDefinition, ARQUILLIAN_XML);

        }

        return null;
    }

    public Path generateAppendedArquillianXml(Class<?> testClass, InputStream originalArquillianXml)
        throws IOException, TransformerException {

        final ChameleonTargetConfiguration chameleonTargetConfiguration = findChameleonTargetConfiguration(testClass);

        if (chameleonTargetConfiguration != null) {

            final Node containerDefinition = chameleonTargetConfiguration.getContainerDefinitionAsXml();
            Document arquillianConfigurationDocument = DomManipulation.createDocumentFromInputStream(originalArquillianXml);

            return generate(arquillianConfigurationDocument, containerDefinition, ARQUILLIAN_CHAMELEON_XML);

        }

        return null;

    }

    private Path generate(Document arquillianConfigurationDocument, Node containerDefinition, String filename)
        throws IOException, TransformerException {
        final Element arquillianElement = (Element) arquillianConfigurationDocument.getFirstChild();
        final Node importNode = arquillianConfigurationDocument.importNode(containerDefinition, true);
        arquillianElement.appendChild(importNode);

        final Path tempDirectory = Files.createTempDirectory("chameleonContainer");
        final Path arquillianPath = tempDirectory.resolve(filename);

        DomManipulation.writeToPath(arquillianConfigurationDocument, arquillianPath);

        return arquillianPath;
    }

    private ChameleonTargetConfiguration findChameleonTargetConfiguration(Class<?> testClass) {
        final Annotation[] annotations = testClass.getAnnotations();

        for (Annotation annotation : annotations) {
            final ChameleonTargetConfiguration chameleonTargetConfiguration = AnnotationExtractor.extract(annotation);

            if (chameleonTargetConfiguration != null) {
                // We only support one container for now.
                return chameleonTargetConfiguration;
            }
        }

        return null;
    }

}
