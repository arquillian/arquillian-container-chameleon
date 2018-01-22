package org.arquillian.container.chameleon.runner;

import java.lang.annotation.Annotation;
import java.util.Map;
import org.arquillian.container.chameleon.api.ChameleonTarget;
import org.arquillian.container.chameleon.api.Mode;
import org.arquillian.container.chameleon.runner.fixtures.GenericTest;
import org.arquillian.container.chameleon.runner.fixtures.Tomcat;
import org.arquillian.container.chameleon.runner.fixtures.Tomcat8;
import org.arquillian.container.chameleon.runner.fixtures.Tomcat8Test;
import org.arquillian.container.chameleon.runner.fixtures.TomcatTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class AnnotationExtractorTest {

    @Test
    public void should_get_roeprties_from_chameleon_annotation() {

        // given
        final ChameleonTarget chameleonTarget = GenericTest.class.getAnnotation(ChameleonTarget.class);

        // when
        final ChameleonTargetConfiguration chameleonTargetConfiguration = AnnotationExtractor.extract(chameleonTarget);

        // then
        assertThat(chameleonTargetConfiguration.getContainer()).isEqualTo("tomcat");
        assertThat(chameleonTargetConfiguration.getVersion()).isEqualTo("7.0.0");
        assertThat(chameleonTargetConfiguration.getMode()).isEqualTo(Mode.MANAGED);
        assertThat(chameleonTargetConfiguration.getCustomProperties()).contains(entry("a", "b"));
    }

    @Test
    public void should__get_properties_from_meta_annotations() {

        // given
        final Tomcat tomcat = TomcatTest.class.getAnnotation(Tomcat.class);

        // when
        final ChameleonTargetConfiguration chameleonTargetConfiguration = AnnotationExtractor.extract(tomcat);

        // then
        assertThat(chameleonTargetConfiguration.getContainer()).isEqualTo("tomcat");
        assertThat(chameleonTargetConfiguration.getVersion()).isEqualTo("7.0.0");
        assertThat(chameleonTargetConfiguration.getMode()).isEqualTo(Mode.MANAGED);

    }

    @Test
    public void should_navigate_through_all_hierarchy_of_configurations() {

        // given
        final Tomcat8 tomcat8 = Tomcat8Test.class.getAnnotation(Tomcat8.class);

        // when
        final ChameleonTargetConfiguration chameleonTargetConfiguration = AnnotationExtractor.extract(tomcat8);

        // then
        assertThat(chameleonTargetConfiguration.getContainer()).isEqualTo("tomcat");
        assertThat(chameleonTargetConfiguration.getVersion()).isEqualTo("8.0.0");
        assertThat(chameleonTargetConfiguration.getMode()).isEqualTo(Mode.MANAGED);

    }

}
