package org.arquillian.container.chameleon;

import org.arquillian.container.chameleon.spi.model.ContainerAdapter;
import org.jboss.arquillian.container.spi.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;

public class ConfigurationTestCase {

    @Test
    public void shouldLoadSimpleAdapterConfiguration() throws Exception {
        ChameleonDeployableContainerConfiguration configuration = new ChameleonDeployableContainerConfiguration();
        configuration.setTarget("wildfly:8.2.0.Final:managed");
        configuration.validate();

        ContainerAdapter adapter = configuration.getConfiguredAdapter();
        Assert.assertEquals("org.wildfly:wildfly-dist:zip:8.2.0.Final", adapter.distribution());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnMissingContainerName() throws Exception {
        ChameleonDeployableContainerConfiguration configuration = new ChameleonDeployableContainerConfiguration();
        configuration.setTarget("MISSING_TARGET:8.2.0.Final:managed");
        configuration.validate();

        configuration.getConfiguredAdapter();
    }

    @Test(expected = ConfigurationException.class)
    public void shouldFailOnMissingContainerType() throws Exception {
        ChameleonDeployableContainerConfiguration configuration = new ChameleonDeployableContainerConfiguration();
        configuration.setTarget("wildfly:8.2.0.Final:UNKNOWN");
        configuration.validate();
    }

    @Test(expected = ConfigurationException.class)
    public void shouldFailOnMissingContainerFile() throws Exception {
        ChameleonDeployableContainerConfiguration configuration = new ChameleonDeployableContainerConfiguration();
        configuration.setContainerConfigurationFile("MISSING");
        configuration.setTarget("wildfly:8.2.0.Final:managed");
        configuration.validate();
    }
}