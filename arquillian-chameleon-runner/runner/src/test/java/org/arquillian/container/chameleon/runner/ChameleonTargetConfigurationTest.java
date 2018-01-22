package org.arquillian.container.chameleon.runner;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.arquillian.container.chameleon.api.Mode;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class ChameleonTargetConfigurationTest {

    @Test
    public void should_override_only_not_set_fields() {

        // given
        final ChameleonTargetConfiguration containerConf = new ChameleonTargetConfiguration("tomcat", "7.0.0", Mode.MANAGED, new HashMap<String, String>());
        final ChameleonTargetConfiguration modeConf = new ChameleonTargetConfiguration(null, null, Mode.EMBEDDED, new HashMap<String, String>());

        // when
        final ChameleonTargetConfiguration chameleonTargetConfiguration = modeConf.importConfiguration(containerConf);

        // then
        assertThat(chameleonTargetConfiguration.getContainer()).isEqualTo("tomcat");
        assertThat(chameleonTargetConfiguration.getVersion()).isEqualTo("7.0.0");
        assertThat(chameleonTargetConfiguration.getMode()).isEqualTo(Mode.EMBEDDED);

    }

    @Test
    public void should_append_custom_properties() {

        // given
        final Map<String, String> original = new HashMap<>();
        original.put("a", "b");
        original.put("c", "d");

        final Map<String, String> newOpts = new HashMap<>();
        original.put("a", "z");
        original.put("y", "x");

        final ChameleonTargetConfiguration containerConf = new ChameleonTargetConfiguration("tomcat", "7.0.0", Mode.MANAGED, original);
        final ChameleonTargetConfiguration modeConf = new ChameleonTargetConfiguration(null, null, Mode.EMBEDDED, newOpts);

        // when
        final ChameleonTargetConfiguration chameleonTargetConfiguration = modeConf.importConfiguration(containerConf);

        // then
        assertThat(chameleonTargetConfiguration.getCustomProperties())
            .containsOnly(entry("a", "z"), entry("y", "x"), entry("c", "d"));
    }

    @Test
    public void should_generate_a_container_node_object() {

        // given
        final ChameleonTargetConfiguration chameleonTargetConfiguration =
            new ChameleonTargetConfiguration("tomcat:8.0.0:managed");

        // when
        final Element containerDefinition = (Element) chameleonTargetConfiguration.getContainerDefinitionAsXml();

        // then
        final NodeList properties = containerDefinition.getElementsByTagName("property");
        assertThat(properties.getLength())
            .isEqualTo(1);
        assertThat(properties.item(0).getTextContent())
            .isEqualTo("tomcat:8.0.0:managed");
    }

    @Test
    public void should_generate_a_container_properties_object() {

        // given
        final ChameleonTargetConfiguration chameleonTargetConfiguration =
            new ChameleonTargetConfiguration("tomcat:8.0.0:managed");

        // when
        final Properties containerDefinitionAsProperties =
            chameleonTargetConfiguration.getContainerDefinitionAsProperties();

        // then
        assertThat(containerDefinitionAsProperties)
            .contains(entry("arq.container.chameleon.configuration.chameleonTarget", "tomcat:8.0.0:managed"));
    }

    @Test
    public void should_generate_a_container_properties_object_with_custom_properties() {

        // given
        Map<String, String> customProps = new HashMap<>();
        customProps.put("a", "b");
        final ChameleonTargetConfiguration chameleonTargetConfiguration =
            new ChameleonTargetConfiguration("tomcat", "8.0.0", Mode.MANAGED, customProps);

        // when
        final Properties containerDefinitionAsProperties =
            chameleonTargetConfiguration.getContainerDefinitionAsProperties();

        // then
        assertThat(containerDefinitionAsProperties)
            .contains(entry("arq.container.chameleon.configuration.a", "b"));
    }

    @Test
    public void should_generate_a_container_node_object_with_custom_properties() {

        // given
        Map<String, String> customProps = new HashMap<>();
        customProps.put("a", "b");
        final ChameleonTargetConfiguration chameleonTargetConfiguration =
            new ChameleonTargetConfiguration("tomcat", "8.0.0", Mode.MANAGED, customProps);

        // when
        final Element containerDefinition = (Element) chameleonTargetConfiguration.getContainerDefinitionAsXml();

        // then
        final NodeList properties = containerDefinition.getElementsByTagName("property");
        assertThat(properties.getLength())
            .isEqualTo(2);
        assertThat(properties.item(0).getTextContent())
            .isEqualTo("tomcat:8.0.0:managed");
        final Element customProperty = (Element) properties.item(1);
        assertThat(customProperty.getAttribute("name"))
            .isEqualTo("a");
        assertThat(customProperty.getTextContent())
            .isEqualTo("b");

    }

}
