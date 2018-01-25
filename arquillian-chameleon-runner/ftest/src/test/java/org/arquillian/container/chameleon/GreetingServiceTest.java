package org.arquillian.container.chameleon;

import javax.inject.Inject;
import org.arquillian.container.chameleon.runner.ArquillianChameleon;
import org.assertj.core.api.Assertions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@Wildfly
@RunWith(ArquillianChameleon.class)
public class GreetingServiceTest {

    @Deployment
    public static WebArchive deployService() {
        return ShrinkWrap.create(WebArchive.class)
            .addClass(GreetingService.class);
    }

    @Inject
    private GreetingService service;

    @Test
    public void should_get_greetings() {
        assertThat(service).isNotNull();
    }

}
