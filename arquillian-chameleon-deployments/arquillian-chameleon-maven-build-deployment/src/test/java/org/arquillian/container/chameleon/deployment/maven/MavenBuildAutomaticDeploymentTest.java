package org.arquillian.container.chameleon.deployment.maven;

import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import static org.assertj.core.api.Assertions.assertThat;

public class MavenBuildAutomaticDeploymentTest {

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test
    public void should_get_archive_from_module() {
        final Archive<?> archive = buildWithTestCaseClass(TestCaseClass.class);

        assertThat(archive.toString())
            .startsWith("arquillian-chameleon-deployment-api");
    }

    @Test
    public void givenTheMavenBuildWithLocalInstallation_whenMavenIsInThePath_thenItMustBuild() {
        final Archive<?> archive = buildWithTestCaseClass(TestCaseLocalMavenClass.class);

        assertThat(archive.toString())
            .startsWith("arquillian-chameleon-deployment-api");
    }

    @Test(expected = IllegalStateException.class) //thrown by shrinkwrap maven resolver
    public void givenTheMavenBuildWithLocalInstallation_whenMavenIsNotInThePath_thenItThrowsAnException() {

        environmentVariables.set("M2_HOME", "/path/to/nowhere");

        MavenBuildAutomaticDeployment mavenBuildAutomaticDeployment = new MavenBuildAutomaticDeployment();

        mavenBuildAutomaticDeployment.build(new TestClass(TestCaseLocalMavenClass.class));

    }

    private Archive<?> buildWithTestCaseClass(Class<?> testClass) {
        MavenBuildAutomaticDeployment mavenBuildAutomaticDeployment = new MavenBuildAutomaticDeployment();
        return mavenBuildAutomaticDeployment.build(new TestClass(testClass));
    }

    @MavenBuild(pom = "../../pom.xml",
        module = "arquillian-chameleon-deployments/arquillian-chameleon-deployment-api"
    )
    static class TestCaseClass {
    }

    @MavenBuild(pom = "../../pom.xml",
        module = "arquillian-chameleon-deployments/arquillian-chameleon-deployment-api",
        useLocalInstallation = true
    )
    static class TestCaseLocalMavenClass {
    }

}
