package org.arquillian.container.chameleon.spi.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class TargetTest {

    @Test
    public void shouldReturnFalseForMissingContainerName() throws Exception {
        Target target = Target.from("MISSING_TARGET:8.2.0.Final:managed");
        assertThat(target.isSupported()).isFalse();
    }

    @Test
    public void shouldReturnTrueForGivenContainerName() throws Exception {
        Target target = Target.from("wildfly:8.2.0.Final:managed");
        assertThat(target.isSupported()).isTrue();
    }

    @Test
    public void shouldReturnTrueForTomcatContainer() throws Exception {
        Target target = Target.from("Tomcat:7.0.47:Remote");
        assertThat(target.isSupported()).isTrue();
    }

    @Test
    public void shouldReturnFalseForInvalidTomcatContainer() throws Exception {
        Target target = Target.from("Tomcat`:7.0.47:Remote");
        assertThat(target.isSupported()).isFalse();
    }

}
