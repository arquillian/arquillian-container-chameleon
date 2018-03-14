package org.arquillian.container.chameleon.deployment.api;

import org.assertj.core.api.JUnitSoftAssertions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.DeploymentConfiguration;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeploymentConfigurationPopulatorTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Mock
    Archive<?> archive;

    @Test
    public void should_generate_default_deployment_content() {

        // given
        // when
        final DeploymentConfiguration deploymentConfiguration =
            DeploymentConfigurationPopulator.populate(new TestClass(DefaultConfigurationTest.class), archive).get();

        // then
        softly.assertThat(deploymentConfiguration.getArchive()).isEqualTo(archive);
        softly.assertThat(deploymentConfiguration.getOverProtocol()).isNull();
        softly.assertThat(deploymentConfiguration.getDescriptor()).isNull();
        softly.assertThat(deploymentConfiguration.getShouldThrowException()).isNull();
        softly.assertThat(deploymentConfiguration.getTargets()).isNull();

        final Deployment deployment = deploymentConfiguration.getDeployment();

        softly.assertThat(deployment.managed()).isTrue();
        softly.assertThat(deployment.order()).isEqualTo(-1);
        softly.assertThat(deployment.testable()).isTrue();
    }

    @Test
    public void should_generate_deployment_custom_content() {

        // given
        // when
        final DeploymentConfiguration deploymentConfiguration =
            DeploymentConfigurationPopulator.populate(new TestClass(ParametrizedConfigurationTest.class), archive).get();

        // then
        softly.assertThat(deploymentConfiguration.getArchive()).isEqualTo(archive);
        softly.assertThat(deploymentConfiguration.getOverProtocol().value()).isEqualTo("https");
        softly.assertThat(deploymentConfiguration.getDescriptor()).isNull();
        softly.assertThat(deploymentConfiguration.getShouldThrowException()).isNull();
        softly.assertThat(deploymentConfiguration.getTargets().value()).isEqualTo("container");

        final Deployment deployment = deploymentConfiguration.getDeployment();

        softly.assertThat(deployment.managed()).isFalse();
        softly.assertThat(deployment.order()).isEqualTo(1);
        softly.assertThat(deployment.testable()).isFalse();

    }

    public static class DefaultConfigurationTest {}

    @DeploymentParameters(testable = false, deploymentName = "dep", overProtocol = "https",
        targetsContainer = "container", managed = false, order = 1)
    public static class ParametrizedConfigurationTest {}

}
