package org.arquillian.container.chameleon;

import org.arquillian.container.chameleon.spi.model.ContainerAdapter;
import org.jboss.arquillian.container.spi.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;

public class ConfigurationTestCase {

    @Test
    public void shouldLoadSimpleAdapterConfiguration() throws Exception {
        ChameleonConfiguration configuration = new ChameleonConfiguration();
        configuration.setChameleonTarget("wildfly:8.2.0.Final:managed");
        configuration.validate();

        ContainerAdapter adapter = configuration.getConfiguredAdapter();
        Assert.assertEquals("org.wildfly:wildfly-dist:zip:8.2.0.Final", adapter.distribution());
    }

    @Test
    public void shouldResolveBuildSystemOutputFolderIfDownloadNotSet() throws Exception {
        ChameleonConfiguration configuration = new ChameleonConfiguration();
        Assert.assertEquals("target", configuration.getChameleonDistributionDownloadFolder());
    }

    @Test
    public void shouldUseSetDownloadFolder() throws Exception {
        ChameleonConfiguration configuration = new ChameleonConfiguration();
        configuration.setChameleonDistributionDownloadFolder("TEST");
        Assert.assertEquals("TEST", configuration.getChameleonDistributionDownloadFolder());
    }

    @Test
    public void shouldSetTempDownloadFolder() throws Exception {
        String tempFolder = "/tmp/";
        System.setProperty("java.io.tmpdir", tempFolder);
        ChameleonConfiguration configuration = new ChameleonConfiguration();
        configuration.setChameleonDistributionDownloadFolder("TMP");
        Assert.assertTrue(configuration.getChameleonDistributionDownloadFolder().contains(tempFolder + "/arquillian_chameleon"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnMissingContainerName() throws Exception {
        ChameleonConfiguration configuration = new ChameleonConfiguration();
        configuration.setChameleonTarget("MISSING_TARGET:8.2.0.Final:managed");
        configuration.validate();

        configuration.getConfiguredAdapter();
    }

    @Test(expected = ConfigurationException.class)
    public void shouldFailOnMissingContainerType() throws Exception {
        ChameleonConfiguration configuration = new ChameleonConfiguration();
        configuration.setChameleonTarget("wildfly:8.2.0.Final:UNKNOWN");
        configuration.validate();
    }

    @Test(expected = ConfigurationException.class)
    public void shouldFailOnMissingContainerFile() throws Exception {
        ChameleonConfiguration configuration = new ChameleonConfiguration();
        configuration.setChameleonContainerConfigurationFile("MISSING");
        configuration.setChameleonTarget("wildfly:8.2.0.Final:managed");
        configuration.validate();
    }
}