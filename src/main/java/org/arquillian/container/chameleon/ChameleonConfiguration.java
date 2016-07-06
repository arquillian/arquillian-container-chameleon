package org.arquillian.container.chameleon;

import org.arquillian.container.chameleon.spi.model.Container;
import org.arquillian.container.chameleon.spi.model.ContainerAdapter;
import org.arquillian.container.chameleon.spi.model.Target;
import org.jboss.arquillian.container.spi.ConfigurationException;
import org.jboss.arquillian.container.spi.client.container.ContainerConfiguration;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ChameleonConfiguration implements ContainerConfiguration {

    private static final String MAVEN_OUTPUT_DIRECTORY = "target";
    private static final String GRADLE_OUTPUT_DIRECTORY = "bin";
    private static final String TMP_FOLDER_EXPRESSION = "TMP";

    private static final String DEFAULT_CONTAINER_MAPPING = "chameleon/default/containers.yaml";

    private String chameleonTarget = null;
    private String chameleonContainerConfigurationFile = null;
    private String chameleonDistributionDownloadFolder = null;
    private String chameleonResolveCacheFolder = null;

    public void validate() throws ConfigurationException {
        if (chameleonTarget == null) {
            throw new ConfigurationException("chameleonTarget must be provided in format server:version:type");
        }

        // Trigger possible Exception case during File/Resource load
        getChameleonContainerConfigurationFileStream();

        File resolveCache = getChameleonResolveCacheFolder();
        if (!resolveCache.exists()) {
            if (!resolveCache.mkdirs()) {
                throw new ConfigurationException("Could not create all resolve cache folders: " + resolveCache);
            }
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

    public InputStream getChameleonContainerConfigurationFileStream() {
        boolean isDefault = false;
        String resource = getChameleonContainerConfigurationFile();
        if (resource == null) {
            resource = DEFAULT_CONTAINER_MAPPING;
            isDefault = true;
        }
        return FileUtils.loadConfiguration(resource, isDefault);
    }

    public void setChameleonContainerConfigurationFile(String containerConfigurationFile) {
        this.chameleonContainerConfigurationFile = containerConfigurationFile;
    }

    public void setChameleonDistributionDownloadFolder(String distributionDownloadFolder) {
        this.chameleonDistributionDownloadFolder = distributionDownloadFolder;
    }

    public void setChameleonResolveCacheFolder(String chameleonResolveCacheFolder) {
        this.chameleonResolveCacheFolder = chameleonResolveCacheFolder;
    }

    public String getChameleonDistributionDownloadFolder() {
        if (chameleonDistributionDownloadFolder != null) {
            if (TMP_FOLDER_EXPRESSION.equalsIgnoreCase(chameleonDistributionDownloadFolder)) {
                chameleonDistributionDownloadFolder = System.getProperty("java.io.tmpdir") + "/arquillian_chameleon";
            }
            return chameleonDistributionDownloadFolder;
        }
        return getOutputDirectory();
    }

    public File getChameleonResolveCacheFolder() {
        if (chameleonResolveCacheFolder != null) {
            return new File(chameleonResolveCacheFolder);
        }
        return new File(new File(getChameleonDistributionDownloadFolder(), "server"), "cache");
    }

    public ContainerAdapter getConfiguredAdapter() throws Exception {
        Target target = getParsedTarget();
        Container[] containers = new ContainerLoader().load(getChameleonContainerConfigurationFileStream(), getChameleonResolveCacheFolder());
        for (Container container : containers) {
            ContainerAdapter adapter = container.matches(target);
            if (adapter != null) {
                return adapter;
            }
        }
        throw new IllegalArgumentException("No container configuration found in " + getChameleonContainerConfigurationFile()
                + " for target " + getChameleonTarget());
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
