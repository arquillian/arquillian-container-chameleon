package org.arquillian.container.chameleon.deployment.maven;

import org.jboss.arquillian.container.test.api.DeploymentConfiguration;
import org.jboss.arquillian.container.test.spi.client.deployment.AutomaticDeployment;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;

public class MavenBuildAutomaticDeployment implements AutomaticDeployment {

    MavenRunner mavenRunner;

    public MavenBuildAutomaticDeployment() {

        mavenRunner = new MavenRunner() {
            public Archive<?> run(MavenBuildDeployment conf) {
                return EmbeddedMaven.forProject(conf.pom())
                    .useMaven3Version(conf.mavenVersion())
                    .setGoals(conf.mavenGoals())
                    .setProfiles(conf.mavenProfiles())
                    .setQuiet()
                    .skipTests(true)
                    .ignoreFailure()
                    .build().getDefaultBuiltArchive();
            }
        };

    }

    public DeploymentConfiguration generateDeploymentScenario(TestClass testClass) {

        if (testClass.isAnnotationPresent(MavenBuildDeployment.class)) {

            final MavenBuildDeployment mavenBuildDeployment = testClass.getAnnotation(MavenBuildDeployment.class);

            final Archive archive = mavenRunner.run(mavenBuildDeployment);

            DeploymentConfiguration.DeploymentContentBuilder deploymentContentBuilder =
                initializeWithDeploymentInformation(mavenBuildDeployment, archive);

            if (isNotEmptyOrNull(mavenBuildDeployment.overProtocol())) {
                deploymentContentBuilder.withOverProtocol(mavenBuildDeployment.overProtocol());
            }

            if (isNotEmptyOrNull(mavenBuildDeployment.targetsContainer())) {
                deploymentContentBuilder.withTargetsContainer(mavenBuildDeployment.targetsContainer());
            }

            if (mavenBuildDeployment.shouldThrowExcetionClass() != ConstantException.class) {
                deploymentContentBuilder.withShouldThrowException(mavenBuildDeployment.shouldThrowExcetionClass(), mavenBuildDeployment.testable());
            }

            return deploymentContentBuilder.get();

        }

        // It is safe to return null, we cannot return Optional since core runs on Java5
        return null;
    }

    private DeploymentConfiguration.DeploymentContentBuilder initializeWithDeploymentInformation(
        MavenBuildDeployment mavenBuildDeployment, Archive archive) {
        DeploymentConfiguration.DeploymentContentBuilder deploymentContentBuilder = new DeploymentConfiguration.DeploymentContentBuilder(archive);
        final DeploymentConfiguration.DeploymentBuilder deploymentBuilder = deploymentContentBuilder.withDeployment()
            .withManaged(mavenBuildDeployment.managed())
            .withOrder(mavenBuildDeployment.order())
            .withTestable(mavenBuildDeployment.testable());

        if (isNotEmptyOrNull(mavenBuildDeployment.deploymentName())) {
            deploymentBuilder.withName(mavenBuildDeployment.deploymentName());
        }
        deploymentContentBuilder = deploymentBuilder.build();
        return deploymentContentBuilder;
    }

    private boolean isNotEmptyOrNull(String value) {
        // When we update Chameleon to Java 8 this can be changed to isEmpty
        return value != null && value.length() > 0;
    }

}
