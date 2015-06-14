package org.arquillian.container.chameleon;

import org.arquillian.container.chameleon.controller.DistributionController;
import org.arquillian.container.chameleon.controller.TargetController;
import org.arquillian.container.chameleon.spi.model.ContainerAdapter;
import org.jboss.arquillian.config.descriptor.api.ContainerDef;
import org.jboss.arquillian.container.spi.client.container.ContainerConfiguration;
import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.arquillian.core.api.Injector;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

public class ChameleonContainer implements DeployableContainer<ContainerConfiguration> {

    private TargetController target;
    private DistributionController distribution;
    private ChameleonConfiguration configuration;

    @Inject
    private Instance<Injector> injectorInst;

    @Override
    public Class<ContainerConfiguration> getConfigurationClass() {
        return target.getConfigurationClass();
    }

    @Override
    public ProtocolDescription getDefaultProtocol() {
        return new ProtocolDescription("Servlet 3.0");
    }

    public boolean isInitiated() {
        return this.configuration != null;
    }

    public void init(ChameleonConfiguration configuration, ContainerDef targetConfiguration) {
        this.configuration = configuration;
        try {
            ContainerAdapter adapter = configuration.getConfiguredAdapter();
            this.target = new TargetController(
                    adapter,
                    injectorInst.get());
            this.distribution = new DistributionController(
                    adapter,
                    configuration.getChameleonDistributionDownloadFolder());

            distribution.setup(targetConfiguration);
        } catch (Exception e) {
            throw new IllegalStateException("Could not setup chameleon container", e);
        }
    }

    @Override
    public void setup(final ContainerConfiguration targetConfiguration) {
        try {
            target.setup(targetConfiguration);
        } catch (Exception e) {
            throw new RuntimeException("Could not setup Chameleon container for " + configuration.getChameleonTarget(), e);
        }
    }

    @Override
    public void start() throws LifecycleException {
        target.start();
    }

    @Override
    public void stop() throws LifecycleException {
        target.stop();
    }

    @Override
    public ProtocolMetaData deploy(final Archive<?> archive) throws DeploymentException {
        return target.deploy(archive);
    }

    @Override
    public void undeploy(final Archive<?> archive) throws DeploymentException {
        target.undeploy(archive);
    }

    @Override
    public void deploy(final Descriptor descriptor) throws DeploymentException {
        target.deploy(descriptor);
    }

    @Override
    public void undeploy(final Descriptor descriptor) throws DeploymentException {
        target.undeploy(descriptor);
    }
}
