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
        // given
        // when
        final Archive<?> archive = buildWithTestCaseClass(TestCaseClass.class);

        //then
        assertThat(archive.toString())
            .startsWith("arquillian-chameleon-deployment-api");
    }

    @Test
    public void should_get_archive_from_module_built_by_local_maven_installation() {

        // given maven in the path
        if (System.getenv("maven.home") == null && System.getenv("M2_HOME") == null) {
            environmentVariables.set("M2_HOME", "/usr/local/maven"); // default location in travis-ci machine.
        }

        // when @MavenBuild useLocalInstallation = true
        final Archive<?> archive = buildWithTestCaseClass(TestCaseLocalMavenClass.class);

        // then
        assertThat(archive.toString())
            .startsWith("arquillian-chameleon-deployment-api");
    }

    @Test(expected = IllegalStateException.class) //thrown by shrinkwrap maven resolver
    public void should_throw_exception_due_to_no_maven_found() {

        // given no maven in the path
        environmentVariables.set("M2_HOME", "/path/to/nowhere");
        environmentVariables.set("maven.home", "/path/to/nowhere");

        // when
        MavenBuildAutomaticDeployment mavenBuildAutomaticDeployment = new MavenBuildAutomaticDeployment();

        //then throws
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
