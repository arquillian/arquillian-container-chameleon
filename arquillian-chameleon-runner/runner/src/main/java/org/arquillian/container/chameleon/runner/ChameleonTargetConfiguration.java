package org.arquillian.container.chameleon.runner;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.arquillian.container.chameleon.api.ChameleonTarget;
import org.arquillian.container.chameleon.api.Mode;
import org.arquillian.container.chameleon.api.Property;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.arquillian.container.chameleon.runner.RunnerExpressionParser.parseExpressions;

public class ChameleonTargetConfiguration {

    private static final int CONTAINER = 0;
    public static final int VERSION = 1;
    public static final int MODE = 2;

    private String container;
    private String version;
    private Mode mode;

    private Map<String, String> customProperties = new HashMap<>();

    protected ChameleonTargetConfiguration(String value) {

        final String[] definition = value.split(":");

        if (definition.length != 3) {
            throw new IllegalArgumentException("Value of Chameleon expression must be of form container:version:mode. For example wildfly:9.0.0.Final:managed. "
                + "Refer to https://github.com/arquillian/arquillian-container-chameleon#supported-containers for the complete list of supported containers.");
        }

        this.container = definition[CONTAINER];
        this.version = definition[VERSION];
        this.mode = Mode.valueOf(definition[MODE].toUpperCase());

    }

    protected ChameleonTargetConfiguration(String container, String version, Mode mode,
        Map<String, String> customProperties) {
        this.container = container;
        this.version = version;
        this.mode = mode;
        this.customProperties = customProperties;
    }


    public static ChameleonTargetConfiguration from(ChameleonTarget chameleonTarget) {

        if (isContainerNotDefinedAsString(chameleonTarget)) {
            return new ChameleonTargetConfiguration(
                parseExpressions(chameleonTarget.container()),
                parseExpressions(chameleonTarget.version()),
                Mode.valueOf(parseExpressions(chameleonTarget.mode()).toUpperCase()),
                toMap(chameleonTarget.customProperties()));
        } else {
            final ChameleonTargetConfiguration chameleonTargetConfiguration =
                new ChameleonTargetConfiguration(chameleonTarget.value());
            chameleonTargetConfiguration.customProperties = toMap(chameleonTarget.customProperties());

            return chameleonTargetConfiguration;
        }

    }

    private static boolean isContainerNotDefinedAsString(ChameleonTarget chameleonTarget) {
        return chameleonTarget.value().trim().isEmpty();
    }

    private static Map<String, String> toMap(Property[] properties) {
        final Map<String, String> map = new HashMap<>();
        for (Property property : properties) {
            map.put(property.name(), parseExpressions(property.value()));
        }

        return map;
    }

    /**
     * This methods copies attribute values that are not set in this object other object
     * @param other object where attribute values are copied in case of not set in this object
     * @return this object with new attribute values set
     */
    public ChameleonTargetConfiguration importConfiguration(ChameleonTargetConfiguration other) {

        if (this.container == null || this.container.isEmpty()) {
            this.container = other.getContainer();
        }

        if (this.version == null || this.version.isEmpty()) {
            this.version = other.getVersion();
        }

        if (this.mode == null) {
            this.mode = other.getMode();
        }

        final Set<Map.Entry<String, String>> entries = other.getCustomProperties().entrySet();

        for (Map.Entry<String, String> entry : entries) {
            if (! this.customProperties.containsKey(entry.getKey())) {
                this.customProperties.put(entry.getKey(), entry.getValue());
            }
        }

        return this;
    }

    public Properties getContainerDefinitionAsProperties() {

        Properties properties = new Properties();
        properties.put("arq.container.chameleon.configuration.chameleonTarget", toChameleonTarget());
        properties.put("arq.container.chameleon.default", "true");

        updateCustomProperties(properties);

        return properties;
    }

    public Node getContainerDefinitionAsXml() {
        Document containerDocument = DomManipulation.createDocumentFromContainerTemplate();

        // we can get directly the properties since template only set one container element
        final NodeList properties = containerDocument.getElementsByTagName("property");

        final Element chameleonTargetProperty = findChameleonTargetProperty(properties);

        if (chameleonTargetProperty == null) {
            throw new IllegalArgumentException("No property chameleonTarget found in container template");
        }

        chameleonTargetProperty.setTextContent(toChameleonTarget());
        updateCustomProperties(containerDocument);

        return containerDocument.getFirstChild();

    }

    private void updateCustomProperties(Properties properties) {
        if (! this.customProperties.isEmpty()) {
            for (Map.Entry<String, String> customProperty : this.customProperties.entrySet()) {
                properties.put(String.format("arq.container.chameleon.configuration.%s", customProperty.getKey()),
                    customProperty.getValue());
            }
        }
    }

    private void updateCustomProperties(Document containerDocument) {
        if (! this.customProperties.isEmpty()) {
            final Element configuration = (Element) containerDocument.getElementsByTagName("configuration").item(0);

            for (Map.Entry<String, String> customProperty : this.customProperties.entrySet()) {
                final Element customPropertyElement = containerDocument.createElement("property");
                customPropertyElement.setAttribute("name", customProperty.getKey());
                customPropertyElement.setTextContent(customProperty.getValue());

                configuration.appendChild(customPropertyElement);

            }
        }
    }

    private Element findChameleonTargetProperty(NodeList properties) {
        for (int i = 0; i < properties.getLength(); i++) {
            final Element property = (Element) properties.item(i);

            if (property.hasAttribute("name")
                && "chameleonTarget".equals(property.getAttribute("name"))) {
                return property;
            }
        }

        return null;
    }

    public String getContainer() {
        return container;
    }

    public String getVersion() {
        return version;
    }

    public Mode getMode() {
        return mode;
    }

    public Map<String, String> getCustomProperties() {
        return customProperties;
    }

    public String toChameleonTarget() {
        return String.format("%s:%s:%s", this.container, this.version, this.mode.mode());
    }

}
