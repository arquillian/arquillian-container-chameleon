/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.arquillian.container.chameleon.controller;

import org.arquillian.container.chameleon.configuration.spi.model.ContainerAdapter;
import org.arquillian.container.chameleon.configuration.spi.model.Target.Type;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.task.net.DownloadTool;
import org.jboss.arquillian.config.descriptor.api.ContainerDef;
import org.jboss.arquillian.core.api.threading.ExecutorService;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static org.arquillian.container.chameleon.configuration.Utils.toMavenCoordinate;

public class DistributionController {

    private static final String PROGRESS_INDICATOR = ".";
    private static final int HALF_A_SECOND = 500;

    private ContainerAdapter targetAdapter;
    private String distributionDownloadFolder;

    public DistributionController(ContainerAdapter targetAdapter, String distributionDownloadFolder) {
        this.targetAdapter = targetAdapter;
        this.distributionDownloadFolder = distributionDownloadFolder;
    }

    public void setup(ContainerDef targetConfiguration, ExecutorService executor) throws Exception {
        if (requireDistribution(targetConfiguration)) {
            updateTargetConfiguration(targetConfiguration, resolveDistribution(executor));
        }
    }

    private boolean requireDistribution(ContainerDef targetConfiguration) {
        if (targetAdapter.requireDistribution() && requiredConfigurationNotSet(targetConfiguration, targetAdapter)) {
            if (targetAdapter.type() == Type.Embedded || targetAdapter.type() == Type.Managed) {
                return true;
            }
        }
        return false;
    }

    private File resolveDistribution(ExecutorService executor) {
        if (targetAdapter.distribution().toLowerCase().startsWith("http")) {
            return downloadUsingHttp();
        }
        return fetchFromMavenRepository(executor);
    }

    private File downloadUsingHttp() {
        final String distribution = targetAdapter.distribution();
        final String serverName = new FileNameFromUrlExtractor(distribution).extract();
        final File targetDirectory = new File(new File(distributionDownloadFolder, "server"),
                serverName);

        if (serverAlreadyDownloaded(targetDirectory)) {
            return getDistributionHome(targetDirectory);
        }

        System.out.println("Arquillian Chameleon: downloading distribution from " + distribution);
        final String targetArchive = targetDirectory + "/" + serverName + ".zip";
        final Execution<File> download = Spacelift.task(DownloadTool.class).from(distribution).to(targetArchive).execute();
        try {
            while (!download.isFinished()) {
                System.out.print(PROGRESS_INDICATOR);
                Thread.sleep(HALF_A_SECOND);
            }
            System.out.print(PROGRESS_INDICATOR);

            final File compressedServer = download.await();
            ShrinkWrap.create(ZipImporter.class, serverName)
                      .importFrom(compressedServer)
                      .as(ExplodedExporter.class)
                      .exportExploded(targetDirectory, ".");
            compressedServer.delete();
            return getDistributionHome(targetDirectory);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private File fetchFromMavenRepository(ExecutorService executor) {
        final MavenCoordinate distributableCoordinate = toMavenCoordinate(targetAdapter.distribution());

        if (distributableCoordinate != null) {
            final File targetDirectory = new File(new File(distributionDownloadFolder, "server"),
                    distributableCoordinate.getArtifactId() + "_" + distributableCoordinate.getVersion());

            if (serverAlreadyDownloaded(targetDirectory)) {
                return getDistributionHome(targetDirectory);
            }

            System.out.println("Arquillian Chameleon: downloading distribution " + distributableCoordinate.toCanonicalForm());
            Future<File> uncompressDirectory = executor.submit(new Callable<File>() {
                @Override
                public File call() throws Exception {
                    return Maven.resolver().resolve(distributableCoordinate.toCanonicalForm())
                            .withoutTransitivity()
                            .asSingle(GenericArchive.class)
                            .as(ExplodedExporter.class)
                            .exportExploded(targetDirectory, ".");
                }
            });

            try {
                while (!uncompressDirectory.isDone()) {
                    System.out.print(PROGRESS_INDICATOR);
                    Thread.sleep(HALF_A_SECOND);
                }
                System.out.println();
                return getDistributionHome(uncompressDirectory.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private boolean serverAlreadyDownloaded(File targetDirectory) {
        final boolean exists = targetDirectory.exists();
        if (!exists) {
            targetDirectory.mkdirs();
        }
        return exists;
    }

    private void updateTargetConfiguration(ContainerDef targetConfiguration, File distributionHome) throws Exception {
        final Map<String, String> values = new HashMap<String, String>();
        values.put("dist", distributionHome.getAbsolutePath());

        for (Map.Entry<String, String> configuration : targetAdapter.resolveConfiguration(values).entrySet()) {
            targetConfiguration.overrideProperty(configuration.getKey(), configuration.getValue());
        }
    }

    private boolean requiredConfigurationNotSet(ContainerDef targetConfiguration, ContainerAdapter adapter) {
        final String[] configurationKeys = adapter.configurationKeys();
        final Map<String, String> targetProperties = targetConfiguration.getContainerProperties();
        for (String key : configurationKeys) {
            if (!targetProperties.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    private File getDistributionHome(File uncompressDirectory) {
        final File[] currentDirectoryContent = uncompressDirectory.listFiles();
        // only one root directory should be here
        if (currentDirectoryContent.length == 1 && currentDirectoryContent[0].isDirectory()) {
            return currentDirectoryContent[0];
        } else {
            // means that the server is uncompressed without a root directory
            return uncompressDirectory;
        }

    }
}
