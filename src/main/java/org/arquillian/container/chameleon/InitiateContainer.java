package org.arquillian.container.chameleon;

import org.jboss.arquillian.config.descriptor.api.ContainerDef;
import org.jboss.arquillian.config.descriptor.impl.ContainerDefImpl;
import org.jboss.arquillian.container.spi.event.SetupContainer;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
import org.jboss.shrinkwrap.descriptor.spi.node.Node;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class InitiateContainer {

    // Change the original configuration so we can forward all config options
    // not 'target' to the delegate container
    public void setup(@Observes EventContext<SetupContainer> setup) throws Exception {
        SetupContainer event = setup.getEvent();
        if (isChameleonContainer(event)) {
            initiateChameleon(event);
        }
        setup.proceed();
    }

    private boolean isChameleonContainer(SetupContainer event) {
        return event.getContainer().getDeployableContainer() instanceof ChameleonContainer;
    }

    private void initiateChameleon(SetupContainer setup) throws NoSuchFieldException, IllegalAccessException {
        ChameleonContainer container = (ChameleonContainer) setup.getContainer().getDeployableContainer();
        ContainerDefImpl containerDef = (ContainerDefImpl) setup.getContainer().getContainerConfiguration();
        Field containerNodeField = ContainerDefImpl.class.getDeclaredField("container");
        if (!containerNodeField.isAccessible()) {
            containerNodeField.setAccessible(true);
        }

        Node node = (Node) containerNodeField.get(containerDef);
        Map<String, String> properties = removeAndMerge(container, node, containerDef);
        if (container.isInitiated() && !properties.containsKey("chameleonTarget")) {
            return;
        }

        ChameleonConfiguration configuration = new ChameleonConfiguration();
        try {
            // Remove the Chameleon container properties from configuration
            setAndRemoveProperties(node, properties, configuration);
            configuration.validate();
        } catch (Exception e) {
            throw new RuntimeException("Could not configure Chameleon container " + setup.getContainerName(), e);
        }
        container.init(configuration, containerDef);
    }

    private Map<String, String> removeAndMerge(ChameleonContainer container, Node node, ContainerDef containerDef) {
        Map<String, String> current = containerDef.getContainerProperties();
        if (!container.isInitiated() || !current.containsKey("chameleonTarget")) {
            return current;
        }

        Map<String, String> original = container.getOriginalContainerConfiguration();
        Map<String, String> currentConfigured = container.getCurrentContainerConfiguration();

        for (Map.Entry<String, String> currentConfiguredEntry : currentConfigured.entrySet()) {
            if (!original.containsKey(currentConfiguredEntry.getKey())) {
                node.getSingle("configuration").removeChild("property@name=" + currentConfiguredEntry.getKey());
            }
        }

        return containerDef.getContainerProperties();
    }

    private void setAndRemoveProperties(Node node, Map<String, String> properties, ChameleonConfiguration configuration) throws Exception {

        for (Method setter : configuration.getClass().getMethods()) {
            if ( // isSetter
                    setter.getName().startsWith("set") &&
                            setter.getReturnType().equals(Void.TYPE) &&
                            setter.getParameterTypes().length == 1
                    ) {
                String propertyName = toCamelCase(setter);
                if (properties.containsKey(propertyName)) {
                    setter.invoke(configuration, properties.get(propertyName));
                    node.getSingle("configuration").removeChild("property@name=" + propertyName);
                }
            }
        }
    }

    private String toCamelCase(Method setter) {
        return new StringBuilder(setter.getName())
                .replace(0, 4,
                        String.valueOf(
                                Character.toLowerCase(
                                        setter.getName().charAt(3))))
                .toString();
    }
}
