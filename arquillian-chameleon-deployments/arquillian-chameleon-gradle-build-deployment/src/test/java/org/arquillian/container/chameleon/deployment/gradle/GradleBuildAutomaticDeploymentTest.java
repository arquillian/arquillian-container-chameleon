package org.arquillian.container.chameleon.deployment.gradle;

import static org.assertj.core.api.Assertions.assertThat;

import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

public class GradleBuildAutomaticDeploymentTest {

    @Test
    public void should_build_archive() {
        GradleBuildAutomaticDeployment gradle = new GradleBuildAutomaticDeployment();
        Archive<?> archive = gradle.build(new TestClass(TestCaseClass.class));

        assertThat(archive.contains("/WEB-INF/classes/org/arquillian/example/helloworld/GreetingService.class"))
        	.isTrue();
    }
    
    @GradleBuild(path="../hello-world-example")
    static class TestCaseClass {
    	
    }

}
