package org.arquillian.container.chameleon;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.arquillian.container.chameleon.spi.model.Container;
import org.arquillian.container.chameleon.spi.model.ContainerAdapter;
import org.arquillian.container.chameleon.spi.model.Target;
import org.jboss.arquillian.container.spi.ConfigurationException;
import org.jboss.arquillian.container.spi.client.container.ContainerConfiguration;

public class ChameleonDeployableContainerConfiguration implements ContainerConfiguration {

    private static final String MAVEN_OUTPUT_DIRECTORY = "target";
    private static final String GRADLE_OUTPUT_DIRECTORY = "bin";
    private static final String TMP_FOLDER_EXPRESSION = "TMP";

    private String target = null;
    private String containerConfigurationFile = "/chameleon/default/containers.yaml";
    private String distributionDownloadFolder  = null;

    @Override
    public void validate() throws ConfigurationException {
        if (target == null) {
            throw new ConfigurationException("target must be provided in format server:version:type");
        }
        if(getClass().getResource(getContainerConfigurationFile()) == null) {
            throw new ConfigurationException("containerConfigurationFile must be provided in. Classloader resource " + getContainerConfigurationFile() + " not found");
        }
        // Try to parse to 'trigger' ConfigurationException
        getParsedTarget();
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

    public void setDistributionDownloadFolder(String distributionDownloadFolder) {
        this.distributionDownloadFolder = distributionDownloadFolder;
    }

    public ContainerAdapter getConfiguredAdapter() throws Exception {
        Target target = getParsedTarget();
        Container[] containers = new ContainerLoader().load(getContainerConfigurationFile());
        for (Container container : containers) {
            ContainerAdapter adapter = container.matches(target);
            if (adapter != null) {
                return adapter;
            }
        }
        throw new IllegalArgumentException("No container configuration found in " + getContainerConfigurationFile()
                + " for target " + getTarget());
    }

    public String getDistributionDownloadFolder() {
        if(distributionDownloadFolder != null) {
            if(TMP_FOLDER_EXPRESSION.equalsIgnoreCase(distributionDownloadFolder)) {
                distributionDownloadFolder = System.getProperty("java.io.tmpdir") + "/arquillian_chameleon";
            }
            return distributionDownloadFolder;
        }
        return getOutputDirectory();
    }

    private Target getParsedTarget() {
        return Target.from(getTarget());
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
