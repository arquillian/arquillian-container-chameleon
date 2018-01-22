package org.arquillian.container.chameleon.runner;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import javax.xml.transform.TransformerException;
import org.arquillian.container.chameleon.runner.fixtures.GenericTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArquillianChameleonConfiguratorTest {

    @Mock
    ClassLoader classLoader;

    @Mock
    InputStream file;

    @Rule
    public final RestoreSystemProperties restoreSystemProperties
        = new RestoreSystemProperties();
    
    @Test
    public void should_configure_arquillian_using_properties_if_it_is_not_configured_as_such()
        throws IOException, TransformerException {
        
        // given
        final ArquillianChameleonConfigurator arquillianChameleonConfigurator = new ArquillianChameleonConfigurator();
        when(classLoader.getResourceAsStream(ArquillianChameleonConfigurationGenerator.ARQUILLIAN_PROPERTIES))
            .thenReturn(null);
        
        // when
        final Path setup = arquillianChameleonConfigurator.setup(GenericTest.class, classLoader);

        // then
        assertThat(setup)
            .hasFileName(ArquillianChameleonConfigurationGenerator.ARQUILLIAN_PROPERTIES)
            .exists();
    }

    @Test
    public void should_configure_arquillian_using_xml_if_it_is_not_configured_as_such_and_configured_with_properties()
        throws IOException, TransformerException {

        // given
        final ArquillianChameleonConfigurator arquillianChameleonConfigurator = new ArquillianChameleonConfigurator();
        when(classLoader.getResourceAsStream(ArquillianChameleonConfigurationGenerator.ARQUILLIAN_PROPERTIES))
            .thenReturn(file);

        // when
        final Path setup = arquillianChameleonConfigurator.setup(GenericTest.class, classLoader);

        // then
        assertThat(setup)
            .hasFileName(ArquillianChameleonConfigurationGenerator.ARQUILLIAN_XML)
            .exists();

    }

    @Test
    public void should_override_arquillian_using_xml_if_it_is_configured_as_such_and_configured_with_properties()
        throws IOException, TransformerException {

        // given
        final ArquillianChameleonConfigurator arquillianChameleonConfigurator = new ArquillianChameleonConfigurator();
        when(classLoader.getResourceAsStream(ArquillianChameleonConfigurationGenerator.ARQUILLIAN_PROPERTIES))
            .thenReturn(file);

        final InputStream arquillian =
            Thread.currentThread().getContextClassLoader().getResourceAsStream("arquillian_xml_extensions.xml");
        when(classLoader.getResourceAsStream(ArquillianChameleonConfigurationGenerator.ARQUILLIAN_XML))
            .thenReturn(arquillian);

        // when
        final Path setup = arquillianChameleonConfigurator.setup(GenericTest.class, classLoader);

        // then
        assertThat(setup)
            .hasFileName(ArquillianChameleonConfigurationGenerator.ARQUILLIAN_CHAMELEON_XML)
            .exists();

        assertThat(System.getProperties())
            .contains(entry("arquillian.xml", ArquillianChameleonConfigurationGenerator.ARQUILLIAN_CHAMELEON_XML));

    }

}
