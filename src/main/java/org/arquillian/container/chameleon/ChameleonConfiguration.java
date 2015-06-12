package org.arquillian.container.chameleon;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.arquillian.container.chameleon.spi.model.Container;
import org.arquillian.container.chameleon.spi.model.ContainerAdapter;
import org.arquillian.container.chameleon.spi.model.Target;
import org.jboss.arquillian.container.spi.ConfigurationException;
import org.jboss.arquillian.container.spi.client.container.ContainerConfiguration;

public class ChameleonConfiguration implements ContainerConfiguration {

    private static final String MAVEN_OUTPUT_DIRECTORY = "target";
    private static final String GRADLE_OUTPUT_DIRECTORY = "bin";
    private static final String TMP_FOLDER_EXPRESSION = "TMP";

    private String chameleonTarget = null;
    private String chameleonContainerConfigurationFile = "/chameleon/default/containers.yaml";
    private String chameleonDistributionDownloadFolder  = null;

    @Override
    public void validate() throws ConfigurationException {
        if (chameleonTarget == null) {
            throw new ConfigurationException("target must be provided in format server:version:type");
        }
        if(getClass().getResource(getChameleonContainerConfigurationFile()) == null) {
            throw new ConfigurationException("containerConfigurationFile must be provided in. Classloader resource " + getChameleonContainerConfigurationFile() + " not found");
        }
        // Try to parse to 'trigger' ConfigurationException
        getParsedTarget();
    }

    public String getChameleonTarget() {
        return chameleonTarget;
    }

    public void setChameleonTarget(String target) {
        this.chameleonTarget = target;
    }

    public String getChameleonContainerConfigurationFile() {
        return chameleonContainerConfigurationFile;
    }

    public void setChameleonContainerConfigurationFile(String containerConfigurationFile) {
        this.chameleonContainerConfigurationFile = containerConfigurationFile;
    }

    public void setChameleonDistributionDownloadFolder(String distributionDownloadFolder) {
        this.chameleonDistributionDownloadFolder = distributionDownloadFolder;
    }

    public ContainerAdapter getConfiguredAdapter() throws Exception {
        Target target = getParsedTarget();
        Container[] containers = new ContainerLoader().load(getChameleonContainerConfigurationFile());
        for (Container container : containers) {
            ContainerAdapter adapter = container.matches(target);
            if (adapter != null) {
                return adapter;
            }
        }
        throw new IllegalArgumentException("No container configuration found in " + getChameleonContainerConfigurationFile()
                + " for target " + getChameleonTarget());
    }

    public String getChameleonDistributionDownloadFolder() {
        if(chameleonDistributionDownloadFolder != null) {
            if(TMP_FOLDER_EXPRESSION.equalsIgnoreCase(chameleonDistributionDownloadFolder)) {
                chameleonDistributionDownloadFolder = System.getProperty("java.io.tmpdir") + "/arquillian_chameleon";
            }
            return chameleonDistributionDownloadFolder;
        }
        return getOutputDirectory();
    }

    private Target getParsedTarget() {
        return Target.from(getChameleonTarget());
    }

    private String getOutputDirectory() {
        if (Files.exists(Paths.get(GRADLE_OUTPUT_DIRECTORY))) {
            return GRADLE_OUTPUT_DIRECTORY;
        } else {
            if (Files.exists(Paths.get(MAVEN_OUTPUT_DIRECTORY))) {
                return MAVEN_OUTPUT_DIRECTORY;
            } else {
                // we assume by default a Maven layout
                return MAVEN_OUTPUT_DIRECTORY;
            }
        }
    }
}
