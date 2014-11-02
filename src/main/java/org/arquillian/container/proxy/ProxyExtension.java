package org.arquillian.container.proxy;

import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.core.spi.LoadableExtension;

public class ProxyExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(DeployableContainer.class, ProxyDeployableContainer.class);
        builder.observer(ProxySetupObserver.class);
    }

}
