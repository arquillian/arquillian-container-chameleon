package org.arquillian.container.chameleon.deployment.maven;

import java.io.File;
import java.util.Arrays;
import org.arquillian.container.chameleon.deployment.api.AbstractAutomaticDeployment;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.EmbeddedMaven;
import org.jboss.shrinkwrap.resolver.api.maven.embedded.pom.equipped.ConfigurationDistributionStage;

public class MavenBuildAutomaticDeployment extends AbstractAutomaticDeployment {

    @Override
    protected Archive<?> build(TestClass testClass) {
        if (testClass.isAnnotationPresent(MavenBuild.class)) {
            final MavenBuild mavenBuildDeployment = testClass.getAnnotation(MavenBuild.class);
            return runBuild(mavenBuildDeployment);
        }

        return null;
    }

    private Archive<?> runBuild(MavenBuild conf) {
        final ConfigurationDistributionStage configurationDistributionStage = EmbeddedMaven.forProject(conf.pom())
            .useMaven3Version(conf.mavenVersion())
            .setGoals(conf.mavenGoals())
            .setProfiles(conf.mavenProfiles())
            .setOffline(conf.offline())
            .setQuiet()
            .skipTests(true);

        if (isNotEmptyOrNull(conf.localRepositoryDirectory())) {
            configurationDistributionStage.setLocalRepositoryDirectory(new File(conf.localRepositoryDirectory()));
        }

        if (isNotEmptyOrNull(conf.mvnOpts())) {
            configurationDistributionStage.setMavenOpts(conf.mvnOpts());
        }

        final String[] properties = conf.properties();

        if (properties.length % 2 != 0) {
            throw new IllegalArgumentException(String.format(
                "Maven properties must be set in an array of pairs key, value, but in %s properties are odd",
                Arrays.toString(properties)));
        }

        for (int i = 0; i < properties.length; i += 2) {
            configurationDistributionStage.addProperty(properties[i], properties[i + 2]);
        }

        return configurationDistributionStage
            .ignoreFailure()
            .build().getDefaultBuiltArchive();
    }

    private boolean isNotEmptyOrNull(String value) {
        // When we update Chameleon to Java 8 this can be changed to isEmpty
        return value != null && value.length() > 0;
    }

}
