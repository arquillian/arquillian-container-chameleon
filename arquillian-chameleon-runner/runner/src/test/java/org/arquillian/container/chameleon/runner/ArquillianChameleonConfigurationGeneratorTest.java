package org.arquillian.container.chameleon.runner;

import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.config.XmlPathConfig;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;
import javax.xml.transform.TransformerException;
import org.arquillian.container.chameleon.runner.fixtures.GenericTest;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;

public class ArquillianChameleonConfigurationGeneratorTest {

    @Test
    public void should_create_a_new_arquillian_properties() throws IOException {
        // given
        ArquillianChameleonConfigurationGenerator arquillianChameleonConfigurationGenerator = new ArquillianChameleonConfigurationGenerator();

        // when
        final Path generateArquillianProperties = arquillianChameleonConfigurationGenerator.generateNewArquillianProperties(GenericTest.class);

        // then
        assertThat(generateArquillianProperties)
            .hasFileName("arquillian.properties")
            .exists();

        Properties properties = new Properties();
        properties.load(new FileReader(generateArquillianProperties.toFile()));

        assertThat(properties)
            .contains(entry("arq.container.chameleon.configuration.chameleonTarget", "tomcat:7.0.0:managed"));

    }

    @Test
    public void should_create_a_new_arquillian_xml() throws IOException, TransformerException {

        // given
        ArquillianChameleonConfigurationGenerator arquillianChameleonConfigurationGenerator = new ArquillianChameleonConfigurationGenerator();

        // when
        final Path generatedConfigurationFile = arquillianChameleonConfigurationGenerator.generateNewArquillianXml(GenericTest.class);

        // then
        assertThat(generatedConfigurationFile)
            .hasFileName(ArquillianChameleonConfigurationGenerator.ARQUILLIAN_XML)
            .exists();

        final XmlPath xmlPath = new XmlPath(generatedConfigurationFile.toUri())
            .setRoot("arquillian");

        final String target = xmlPath
            .getString("container.configuration.property.find{it.@name == 'chameleonTarget'}");
        assertThat(target)
            .isNotNull()
            .isEqualTo("tomcat:7.0.0:managed");

        final String a = xmlPath
            .getString("container.configuration.property.find{it.@name == 'a'}");
        assertThat(a)
            .isNotNull()
            .isEqualTo("b");
    }

    @Test
    public void should_append_configured_container_into_existing_arquillian_with_extensions()
        throws IOException, TransformerException {

        // given
        final InputStream arquillianXml = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("arquillian_xml_extensions.xml");

        ArquillianChameleonConfigurationGenerator arquillianChameleonConfigurationGenerator = new ArquillianChameleonConfigurationGenerator();

        // when
        final Path generatedConfigurationFile = arquillianChameleonConfigurationGenerator.generateAppendedArquillianXml(GenericTest.class, arquillianXml);

        // then
        assertThat(generatedConfigurationFile)
            .hasFileName(ArquillianChameleonConfigurationGenerator.ARQUILLIAN_CHAMELEON_XML)
            .exists();

        final XmlPath xmlPath = new XmlPath(generatedConfigurationFile.toUri())
            .setRoot("arquillian");

        final String target = xmlPath
            .getString("container.configuration.property.find{it.@name == 'chameleonTarget'}");

        assertThat(target)
            .isNotNull()
            .isEqualTo("tomcat:7.0.0:managed");

        final String extension = xmlPath
            .getString("extension.@qualifier");

        assertThat(extension)
            .isEqualTo("docker");

    }

    @Test
    public void should_append_configured_container_into_existing_arquillian_with_extensions_and_containers()
        throws IOException, TransformerException {

        // given
        final InputStream arquillianXml = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("arquillian_xml_container_extension.xml");

        ArquillianChameleonConfigurationGenerator arquillianChameleonConfigurationGenerator = new ArquillianChameleonConfigurationGenerator();

        // when
        final Path generatedConfigurationFile = arquillianChameleonConfigurationGenerator.generateAppendedArquillianXml(GenericTest.class, arquillianXml);

        // then
        assertThat(generatedConfigurationFile)
            .exists();

        final XmlPath xmlPath = new XmlPath(generatedConfigurationFile.toUri())
            .setRoot("arquillian");

        final String target = xmlPath
            .getString("container.configuration.property.find{it.@name == 'chameleonTarget'}");

        assertThat(target)
            .isNotNull()
            .isEqualTo("tomcat:7.0.0:managed");

        final String extension = xmlPath
            .getString("extension.@qualifier");

        assertThat(extension)
            .isEqualTo("docker");

        final String propertyOfOldContainer = xmlPath
            .getString("container.configuration.property.find{it.@name == 'host'}");

        assertThat(propertyOfOldContainer)
            .isEqualTo("localhost");

    }

}
