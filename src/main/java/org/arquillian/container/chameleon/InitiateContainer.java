package org.arquillian.container.chameleon;

import java.lang.reflect.Field;
import java.util.Map;

import org.jboss.arquillian.config.descriptor.impl.ContainerDefImpl;
import org.jboss.arquillian.container.spi.event.SetupContainer;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
import org.jboss.shrinkwrap.descriptor.spi.node.Node;

public class InitiateContainer {

    // Change the original configuration so we can forward all config options
    // not 'target' to the delegate container
    public void setup(@Observes EventContext<SetupContainer> setup) throws Exception {
        if(isChameleonContainer(setup.getEvent())) {
            initiateChameleon(setup);
        }
        setup.proceed();
    }

    private boolean isChameleonContainer(SetupContainer event) {
        return event.getContainer().getDeployableContainer() instanceof ChameleonContainer;
    }

    private void initiateChameleon(EventContext<SetupContainer> setup)
            throws NoSuchFieldException, IllegalAccessException {
        ContainerDefImpl containerDef = (ContainerDefImpl) setup.getEvent().getContainer().getContainerConfiguration();
        Field containerNodeField = ContainerDefImpl.class.getDeclaredField("container");
        if (!containerNodeField.isAccessible()) {
            containerNodeField.setAccessible(true);
        }

        Node node = (Node) containerNodeField.get(containerDef);
        Map<String, String> properties = containerDef.getContainerProperties();

        // Remove the Chameleon container properties from configuration
        ChameleonConfiguration configuration = new ChameleonConfiguration();

        if(properties.containsKey("target")) {
            configuration.setTarget(properties.get("target"));
        }
        if(properties.containsKey("containerConfigurationFile")) {
            configuration.setContainerConfigurationFile(properties.get("containerConfigurationFile"));
        }
        if(properties.containsKey("distributionDownloadFolder")) {
            configuration.setDistributionDownloadFolder(properties.get("distributionDownloadFolder"));
        }
        configuration.validate();
        for (String key : new String[] {"target", "containerConfigurationFile", "distributionDownloadFolder"}) {
            node.getSingle("configuration").removeChild("property@name=" + key);
        }

        ChameleonContainer container = (ChameleonContainer) setup.getEvent().getContainer().getDeployableContainer();
        container.init(configuration, containerDef);
    }
}
