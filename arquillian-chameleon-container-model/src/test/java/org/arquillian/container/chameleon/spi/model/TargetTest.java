package org.arquillian.container.chameleon.spi.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TargetTest {

    @Test
    public void should_not_support_for_missing_container() throws Exception {
        Target target = Target.from("MISSING_TARGET:8.2.0.Final:managed");
        assertThat(target.isSupported()).isFalse();
    }

    @Test
    public void should_support_for_valid_wildfly_container() throws Exception {
        Target target = Target.from("wildfly:8.2.0.Final:managed");
        assertThat(target.isSupported()).isTrue();
    }

    @Test
    public void should_support_for_valid_tomcat_container() throws Exception {
        Target target = Target.from("Tomcat:7.0.47:Remote");
        assertThat(target.isSupported()).isTrue();
    }

    @Test
    public void should_not_support_for_invalid_tomcat_container() throws Exception {
        Target target = Target.from("Tomcat`:7.0.47:Remote");
        assertThat(target.isSupported()).isFalse();
    }

    @Test
    public void target_should_supported_for_jboss_as_7_0_1_Container() throws Exception {
        final Target target = Target.from("JBoss AS:7.0.1.Final:managed");
        assertThat(target.isSupported()).isTrue();
    }

    @Test
    public void target_should_supported_for_jboss_as_7_0_2_Container() throws Exception {
        final Target target = Target.from("JBoss AS:7.0.2.Final:remote");
        assertThat(target.isSupported()).isTrue();
    }

    @Test
    public void target_should_not_supported_for_jboss_as_7_1_3_container() throws Exception {
        final Target target = Target.from("JBoss AS:7.1.3.Final:managed");
        assertThat(target.isSupported()).isFalse();
    }

    @Test
    public void target_should_supported_for_jboss_as_7_1_1_container() throws Exception {
        final Target target = Target.from("JBoss AS:7.1.1.Final:embedded");
        assertThat(target.isSupported()).isTrue();
    }

    @Test
    public void target_should_not_supported_for_jboss_as_7_1_2_container() throws Exception {
        final Target target = Target.from("JBoss AS:7.1.2.Final:managed");
        assertThat(target.isSupported()).isFalse();
    }
}
