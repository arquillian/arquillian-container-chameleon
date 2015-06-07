package org.arquillian.container.chameleon;

import static org.arquillian.container.chameleon.Utils.toMavenCoordinate;
import static org.arquillian.container.chameleon.Utils.toMavenDependencies;
import static org.arquillian.container.chameleon.Utils.toURLs;

import java.io.File;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.arquillian.container.chameleon.spi.model.ContainerAdapter;
import org.jboss.arquillian.container.impl.MapObject;
import org.jboss.arquillian.container.spi.client.container.ContainerConfiguration;
import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.arquillian.core.api.Injector;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;

public class ChameleonDeployableContainer implements DeployableContainer<ChameleonDeployableContainerConfiguration> {

    private ClassLoader classloader;
    @SuppressWarnings("rawtypes")
    private DeployableContainer delegate;
    private Map<String, String> delegateConfiguration;

    @Inject
    private Instance<Injector> injectorInst;

    @Override
    public Class<ChameleonDeployableContainerConfiguration> getConfigurationClass() {
        return ChameleonDeployableContainerConfiguration.class;
    }

    @Override
    public ProtocolDescription getDefaultProtocol() {
        return new ProtocolDescription("Servlet 3.0");
    }

    // Called from ProxySetupObserver
    public void setDelegateConfiguration(Map<String, String> delegateConfiguration) {
        this.delegateConfiguration = delegateConfiguration;
    }

    @Override
    public void setup(ChameleonDeployableContainerConfiguration configuration) {

        try {
            ContainerAdapter adapter = configuration.getConfiguredAdapter();

            if (enableDefaultConfigurationProperties(adapter) && adapter.requireDistribution()) {
                switch (adapter.type()) {
                case Embedded:
                case Managed: {
                    File serverHome = resolveDistributablePackage(adapter, configuration);
                    setDefaultConfigurationProperties(adapter, serverHome);
                    break;
                }
                default:
                    break;
                }
            }
            resolveClasspathDependencies(adapter);
        } catch (Exception e) {
            throw new IllegalStateException("Could not setup chameleon container", e);
        }
    }

    private File resolveDistributablePackage(ContainerAdapter adapter, ChameleonDeployableContainerConfiguration configuration) {
        MavenCoordinate distributableCoordinate = toMavenCoordinate(adapter.distribution());

        if (distributableCoordinate != null) {
            File targetDirectory = new File(new File(configuration.getDistributionDownloadFolder(), "server"),
                    distributableCoordinate.getArtifactId() + "_" + distributableCoordinate.getVersion());

            if (targetDirectory.exists()) {
                return getServerHome(targetDirectory);
            } else {
                targetDirectory.mkdirs();
            }

            File uncompressDirectory = Maven.resolver().resolve(distributableCoordinate.toCanonicalForm())
                        .withoutTransitivity()
                        .asSingle(GenericArchive.class)
                        .as(ExplodedExporter.class)
                        .exportExploded(targetDirectory, ".");
            return getServerHome(uncompressDirectory);
        }
        return null;
    }

    private File getServerHome(File uncompressDirectory) {
        File[] currentDirectoryContent = uncompressDirectory.listFiles();
        // only one root directory should be here
        if (currentDirectoryContent.length == 1 && currentDirectoryContent[0].isDirectory()) {
            return currentDirectoryContent[0];
        } else {
            // means that the server is uncompressed without a root directory
            return uncompressDirectory;
        }

    }

    private boolean enableDefaultConfigurationProperties(ContainerAdapter adapter) {
        String[] configurationKeys = adapter.configurationKeys();
        for (String key : configurationKeys) {
            if (delegateConfiguration.containsKey(key)) {
                return false;
            }
        }
        return true;
    }

    private void setDefaultConfigurationProperties(ContainerAdapter adapter, File serverHome) {
        Map<String, String> values = new HashMap<String, String>();
        values.put("dist", serverHome.getAbsolutePath());

        Map<String, String> configuration = adapter.resolveConfiguration(values);
        Set<Entry<String, String>> entrySet = configuration.entrySet();
        for (Entry<String, String> property : entrySet) {
            String propertyKey = property.getKey();
            if (isSystemPropertyNotSet(propertyKey) && !delegateConfiguration.containsKey(propertyKey)) {
                delegateConfiguration.put(propertyKey, property.getValue());
            }
        }
    }

    private boolean isSystemPropertyNotSet(String systemProperty) {
        return System.getProperty(systemProperty) == null;
    }

    private void resolveClasspathDependencies(ContainerAdapter profile) {
        String[] dependencies = profile.dependencies();

        try {
            MavenDependency[] mavenDependencies = toMavenDependencies(dependencies, profile.excludes());

            File[] archives = Maven.configureResolver().addDependencies(mavenDependencies).resolve().withTransitivity()
                    .asFile();
            classloader = new URLClassLoader(toURLs(archives), ChameleonDeployableContainer.class.getClassLoader());

            final Class<?> delegateClass = classloader.loadClass(profile.adapterClass());
            lifecycle(new Callable<Void>() {
                @SuppressWarnings({ "rawtypes", "unchecked" })
                @Override
                public Void call() throws Exception {
                    delegate = (DeployableContainer) delegateClass.newInstance();
                    injectorInst.get().inject(delegate);

                    ContainerConfiguration delegateConfig = (ContainerConfiguration) delegate.getConfigurationClass()
                            .newInstance();
                    MapObject.populate(delegateConfig, delegateConfiguration);
                    delegateConfig.validate();
                    delegate.setup(delegateConfig);
                    return null;
                }
            });

        } catch (Exception e) {
            throw new RuntimeException("Could not setup chameleon container", e);
        }
    }

    @Override
    public void start() throws LifecycleException {
        lifecycle(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                delegate.start();
                return null;
            }
        });
    }

    @Override
    public void stop() throws LifecycleException {
        lifecycle(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                delegate.stop();
                return null;
            }
        });
    }

    @Override
    public ProtocolMetaData deploy(final Archive<?> archive) throws DeploymentException {
        return deployment(new Callable<ProtocolMetaData>() {
            @SuppressWarnings("unchecked")
            @Override
            public ProtocolMetaData call() throws Exception {
                return delegate.deploy(archive);
            }
        });
    }

    @Override
    public void undeploy(final Archive<?> archive) throws DeploymentException {
        deployment(new Callable<Void>() {
            @SuppressWarnings("unchecked")
            @Override
            public Void call() throws Exception {
                delegate.undeploy(archive);
                return null;
            }
        });
    }

    @Override
    public void deploy(final Descriptor descriptor) throws DeploymentException {
        deployment(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                delegate.deploy(descriptor);
                return null;
            }
        });
    }

    @Override
    public void undeploy(final Descriptor descriptor) throws DeploymentException {
        deployment(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                delegate.undeploy(descriptor);
                return null;
            }
        });
    }

    private <T> T deployment(Callable<T> callable) throws DeploymentException {
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classloader);
            return callable.call();
        } catch (DeploymentException e) {
            throw e;
        } catch (Exception e) {
            throw new DeploymentException("Could not proxy call", e);
        } finally {
            Thread.currentThread().setContextClassLoader(current);
        }
    }

    private <T> T lifecycle(Callable<T> callable) throws LifecycleException {
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classloader);
            return callable.call();
        } catch (LifecycleException e) {
            throw e;
        } catch (Exception e) {
            throw new LifecycleException("Could not proxy call", e);
        } finally {
            Thread.currentThread().setContextClassLoader(current);
        }
    }
}
