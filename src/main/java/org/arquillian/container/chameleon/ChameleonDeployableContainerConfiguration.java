package org.arquillian.container.chameleon;

import org.arquillian.container.chameleon.spi.model.Container;
import org.arquillian.container.chameleon.spi.model.ContainerAdapter;
import org.arquillian.container.chameleon.spi.model.Target;
import org.jboss.arquillian.container.spi.ConfigurationException;
import org.jboss.arquillian.container.spi.client.container.ContainerConfiguration;

public class ChameleonDeployableContainerConfiguration implements ContainerConfiguration {

    private String target = null;
    private String containerConfigurationFile = "/chameleon/default/containers.yaml";

    @Override
    public void validate() throws ConfigurationException {
        if (target == null) {
            throw new ConfigurationException("target must be provided in format server:version:type");
        }
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getContainerConfigurationFile() {
        return containerConfigurationFile;
    }

    public void setContainerConfigurationFile(String containerConfigurationFile) {
        this.containerConfigurationFile = containerConfigurationFile;
    }

    public ContainerAdapter getConfiguredAdapter() throws Exception {
        Target target = getParsedTarget();
        Container[] containers = new ServerLoader().load(getContainerConfigurationFile());
        for (Container container : containers) {
            ContainerAdapter adapter = container.matches(target);
            if (adapter != null) {
                return adapter;
            }
        }
        throw new IllegalArgumentException("No container configuration found in " + getContainerConfigurationFile()
                + " for target " + getTarget());
    }

    private Target getParsedTarget() {
        return Target.from(getTarget());
    }
}
