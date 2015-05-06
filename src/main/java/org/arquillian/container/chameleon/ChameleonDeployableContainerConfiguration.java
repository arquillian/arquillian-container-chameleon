package org.arquillian.container.chameleon;

import org.arquillian.container.chameleon.spi.Profile;
import org.arquillian.container.chameleon.spi.Target;
import org.jboss.arquillian.container.spi.ConfigurationException;
import org.jboss.arquillian.container.spi.client.container.ContainerConfiguration;

public class ChameleonDeployableContainerConfiguration implements ContainerConfiguration {

    private String target = null;

    @Override
    public void validate() throws ConfigurationException {
        if(target == null) {
            throw new ConfigurationException("target must be provided in format server:version:type");
        }
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Profile getProfile() {
        return Profile.from(Target.from(this.target));
    }
}
