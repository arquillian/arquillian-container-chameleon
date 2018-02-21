package org.arquillian.container.chameleon.deployment.api;

import org.jboss.arquillian.container.test.api.DeploymentConfiguration;
import org.jboss.arquillian.container.test.spi.client.deployment.AutomaticDeployment;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;

public abstract class AbstractAutomaticDeployment implements AutomaticDeployment {

    @Override
    public DeploymentConfiguration generateDeploymentScenario(TestClass testClass) {
        final Archive<?> archive = build(testClass);

        if (archive != null) {

            DeploymentConfiguration.DeploymentContentBuilder deploymentContentBuilder =
                DeploymentConfigurationPopulator.populate(testClass, archive);

            return deploymentContentBuilder.get();
        }

        // It is safe to return null, we cannot return Optional since core runs on Java7
        return null;
    }

    protected abstract Archive<?> build(TestClass testClass);

}
