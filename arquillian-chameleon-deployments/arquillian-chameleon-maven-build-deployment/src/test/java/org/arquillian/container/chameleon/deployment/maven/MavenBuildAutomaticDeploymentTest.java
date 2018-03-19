package org.arquillian.container.chameleon.deployment.maven;

import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MavenBuildAutomaticDeploymentTest {

    @Test
    public void should_get_archive_from_module() {

        // given
        MavenBuildAutomaticDeployment mavenBuildAutomaticDeployment = new MavenBuildAutomaticDeployment();

        // when
        final Archive<?> archive = mavenBuildAutomaticDeployment.build(new TestClass(TestCaseClass.class));

        // then
        assertThat(archive.toString())
            .startsWith("arquillian-chameleon-deployment-api");
    }

    @MavenBuild(pom = "../../pom.xml",
        module = "arquillian-chameleon-deployments/arquillian-chameleon-deployment-api"
    )
    static class TestCaseClass {
    }

}
