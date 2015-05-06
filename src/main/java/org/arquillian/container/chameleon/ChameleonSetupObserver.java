package org.arquillian.container.chameleon;

import java.lang.reflect.Field;
import java.util.Map;

import org.jboss.arquillian.config.descriptor.impl.ContainerDefImpl;
import org.jboss.arquillian.container.spi.event.SetupContainer;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
import org.jboss.shrinkwrap.descriptor.spi.node.Node;

public class ChameleonSetupObserver {

    // Change the original configuration so we can forward all config options not 'target' to the delegate container
    public void setup(@Observes EventContext<SetupContainer> setup) throws Exception {
        ContainerDefImpl containerDef = (ContainerDefImpl)setup.getEvent().getContainer().getContainerConfiguration();
        Field containerNodeField = ContainerDefImpl.class.getDeclaredField("container");
        if(!containerNodeField.isAccessible()) {
            containerNodeField.setAccessible(true);
        }

        Node node = (Node)containerNodeField.get(containerDef);

        Map<String, String> properties = containerDef.getContainerProperties();
        properties.remove("target"); // The only option the proxy support
        for(String key : properties.keySet()) {
            node.getSingle("configuration").removeChild("property@name=" + key);
        }

        ((ChameleonDeployableContainer)setup.getEvent().getContainer().getDeployableContainer()).setDelegateConfiguration(properties);

        setup.proceed();
    }
}
