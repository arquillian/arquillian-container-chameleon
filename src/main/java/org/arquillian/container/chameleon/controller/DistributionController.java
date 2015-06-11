package org.arquillian.container.chameleon.controller;

import static org.arquillian.container.chameleon.Utils.toMavenCoordinate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.arquillian.container.chameleon.spi.model.ContainerAdapter;
import org.arquillian.container.chameleon.spi.model.Target.Type;
import org.jboss.arquillian.config.descriptor.api.ContainerDef;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;

public class DistributionController {

    private ContainerAdapter targetAdapter;
    private String distributionDownloadFolder;

    public DistributionController(ContainerAdapter targetAdapter, String distributionDownloadFolder) {
        this.targetAdapter = targetAdapter;
        this.distributionDownloadFolder = distributionDownloadFolder;
    }

    public void setup(ContainerDef targetConfiguration) throws Exception {
        if(requireDistribution(targetConfiguration)) {
            updateTargetConfiguration(targetConfiguration, resolveDistribution());
        }
    }

    private boolean requireDistribution(ContainerDef targetConfiguration) {
        if (targetAdapter.requireDistribution() && requiredConfigurationNotSet(targetConfiguration, targetAdapter)) {
            if(targetAdapter.type() == Type.Embedded || targetAdapter.type() == Type.Managed) {
                return true;
            }
        }
        return false;
    }

    private File resolveDistribution() {
        MavenCoordinate distributableCoordinate = toMavenCoordinate(targetAdapter.distribution());

        if (distributableCoordinate != null) {
            File targetDirectory = new File(new File(distributionDownloadFolder, "server"),
                    distributableCoordinate.getArtifactId() + "_" + distributableCoordinate.getVersion());

            if (targetDirectory.exists()) {
                return getDistributionHome(targetDirectory);
            } else {
                targetDirectory.mkdirs();
            }

            File uncompressDirectory = Maven.resolver().resolve(distributableCoordinate.toCanonicalForm())
                        .withoutTransitivity()
                        .asSingle(GenericArchive.class)
                        .as(ExplodedExporter.class)
                        .exportExploded(targetDirectory, ".");
            return getDistributionHome(uncompressDirectory);
        }
        return null;
    }

    private void updateTargetConfiguration(ContainerDef targetConfiguration, File distributionHome) throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        values.put("dist", distributionHome.getAbsolutePath());

        for(Map.Entry<String, String> configuration : targetAdapter.resolveConfiguration(values).entrySet()) {
            targetConfiguration.overrideProperty(configuration.getKey(), configuration.getValue());
        }
    }

    private boolean requiredConfigurationNotSet(ContainerDef targetConfiguration, ContainerAdapter adapter) {
        String[] configurationKeys = adapter.configurationKeys();
        Map<String, String> targetProperties = targetConfiguration.getContainerProperties();
        for (String key : configurationKeys) {
            if(!targetProperties.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    private File getDistributionHome(File uncompressDirectory) {
        File[] currentDirectoryContent = uncompressDirectory.listFiles();
        // only one root directory should be here
        if (currentDirectoryContent.length == 1 && currentDirectoryContent[0].isDirectory()) {
            return currentDirectoryContent[0];
        } else {
            // means that the server is uncompressed without a root directory
            return uncompressDirectory;
        }

    }
}
