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
import org.jboss.arquillian.core.api.threading.ExecutorService;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

import java.util.HashMap;
import java.util.Map;

public class ChameleonContainer implements DeployableContainer<ContainerConfiguration> {

    private TargetController target;
    private DistributionController distribution;
    private ChameleonConfiguration configuration;
    private Map<String, String> originalContainerConfiguration;
    private Map<String, String> currentContainerConfiguration;

    @Inject
    private Instance<Injector> injectorInst;

    @Inject
    private Instance<ExecutorService> executorServiceInst;

    public Class<ContainerConfiguration> getConfigurationClass() {
        return target.getConfigurationClass();
    }

    public ProtocolDescription getDefaultProtocol() {
        return target.getDefaultProtocol();
    }

    public boolean isInitiated() {
        return this.configuration != null;
    }

    Map<String, String> getOriginalContainerConfiguration() {
        return originalContainerConfiguration;
    }

    Map<String, String> getCurrentContainerConfiguration() {
        return currentContainerConfiguration;
    }

    public void init(ChameleonConfiguration configuration, ContainerDef targetConfiguration) {
        this.configuration = configuration;
        if (this.originalContainerConfiguration == null) {
            this.originalContainerConfiguration = new HashMap<String, String>(targetConfiguration.getContainerProperties());
        }
        try {
            ContainerAdapter adapter = configuration.getConfiguredAdapter();
            this.target = new TargetController(
                    adapter,
                    injectorInst.get(),
                    configuration);
            this.distribution = new DistributionController(
                    adapter,
                    configuration.getChameleonDistributionDownloadFolder());

            distribution.setup(targetConfiguration, executorServiceInst.get(), configuration.getChameleonSettingsXml());
        } catch (Exception e) {
            throw new IllegalStateException("Could not setup chameleon container", e);
        }
        this.currentContainerConfiguration = targetConfiguration.getContainerProperties();
    }

    public Class<?> resolveTargetClass(String className) throws ClassNotFoundException {
        if (isInitiated()) {
            return this.target.getClassLoader().loadClass(className);
        }
        throw new RuntimeException("Chameleon container is not yet initialized. No Classloader to load from");
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
