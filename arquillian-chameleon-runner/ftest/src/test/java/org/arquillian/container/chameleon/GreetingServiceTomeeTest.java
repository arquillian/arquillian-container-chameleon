package org.arquillian.container.chameleon;

import org.arquillian.container.chameleon.runner.ArquillianChameleon;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@Tomee
@RunWith(ArquillianChameleon.class)
public class GreetingServiceTomeeTest {

    @Deployment
    public static WebArchive deployService() {
        return ShrinkWrap.create(WebArchive.class)
            .addClass(GreetingService.class);
    }

    @Inject
    private GreetingService service;

    @Test
    public void should_get_greetings() {
        assertThat(service, is(notNullValue()));
    }

}
