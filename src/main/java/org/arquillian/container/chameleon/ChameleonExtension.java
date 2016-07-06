package org.arquillian.container.chameleon;

import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.core.spi.LoadableExtension;

public class ChameleonExtension implements LoadableExtension {

    public void register(ExtensionBuilder builder) {
        builder.service(DeployableContainer.class, ChameleonContainer.class);
        builder.observer(InitiateContainer.class);
    }

}
