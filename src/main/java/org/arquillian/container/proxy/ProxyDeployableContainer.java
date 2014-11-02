package org.arquillian.container.proxy;

import static org.arquillian.container.proxy.Utils.toMavenDependencies;
import static org.arquillian.container.proxy.Utils.toURLs;
import static org.arquillian.container.proxy.Utils.join;

import java.io.File;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.Callable;

import org.arquillian.container.proxy.spi.Profile;
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
import org.jboss.shrinkwrap.descriptor.api.Descriptor;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependency;

public class ProxyDeployableContainer implements DeployableContainer<ProxyDeployableContainerConfiguration> {

    private ClassLoader classloader;
    private DeployableContainer delegate;
    private Map<String, String> delegateConfiguration;

    @Inject
    private Instance<Injector> injectorInst;

    @Override
    public Class<ProxyDeployableContainerConfiguration> getConfigurationClass() {
        return ProxyDeployableContainerConfiguration.class;
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
    public void setup(ProxyDeployableContainerConfiguration configuration) {

        Profile profile = configuration.getProfile();
        String[] dependencies = profile.getDependencies();
        System.out.println("Resolving dependencies:\n" + join(dependencies));

        try {
            MavenDependency[] mavenDependencies = toMavenDependencies(dependencies, profile.getExclusions());

            File[] archives = Maven.configureResolver().addDependencies(mavenDependencies).resolve().withTransitivity().asFile();
            System.out.println("Resolved:\n" + join(archives));
            classloader = new URLClassLoader(toURLs(archives), ProxyDeployableContainer.class.getClassLoader());

            final Class<?> delegateClass = classloader.loadClass(profile.getDeloyableContainerClass());

            lifecycle(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    delegate = (DeployableContainer) delegateClass.newInstance();
                    injectorInst.get().inject(delegate);

                    ContainerConfiguration delegateConfig = (ContainerConfiguration)delegate.getConfigurationClass().newInstance();
                    MapObject.populate(delegateConfig, delegateConfiguration);
                    delegateConfig.validate();
                    delegate.setup(delegateConfig);
                    return null;
                }
            });

        }
        catch (Exception e) {
            throw new RuntimeException("Could not setup proxy", e);
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
            @Override
            public ProtocolMetaData call() throws Exception {
                return delegate.deploy(archive);
            }
        });
    }

    @Override
    public void undeploy(final Archive<?> archive) throws DeploymentException {
        deployment(new Callable<Void>() {
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
        }
        catch(DeploymentException e) {
            throw e;
        }
        catch(Exception e ) {
            throw new DeploymentException("Could not proxy call", e);
        }
        finally {
            Thread.currentThread().setContextClassLoader(current);
        }
    }

    private <T> T lifecycle(Callable<T> callable) throws LifecycleException {
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classloader);
            return callable.call();
        }
        catch(LifecycleException e) {
            throw e;
        }
        catch(Exception e ) {
            throw new LifecycleException("Could not proxy call", e);
        }
        finally {
            Thread.currentThread().setContextClassLoader(current);
        }
    }
}
