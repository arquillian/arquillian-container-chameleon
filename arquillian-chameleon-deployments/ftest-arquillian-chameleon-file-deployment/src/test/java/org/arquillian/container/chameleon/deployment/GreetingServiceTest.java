package org.arquillian.container.chameleon.deployment;

import java.net.URL;
import org.arquillian.container.chameleon.api.ChameleonTarget;
import org.arquillian.container.chameleon.deployment.api.DeploymentParameters;
import org.arquillian.container.chameleon.deployment.file.File;
import org.arquillian.container.chameleon.runner.ArquillianChameleon;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.restassured.RestAssured.given;

@RunWith(ArquillianChameleon.class)
@ChameleonTarget("wildfly:9.0.0.Final:managed")
@DeploymentParameters(testable = false)
@File("src/test/resources/arquillian-example-helloworld.war")
public class GreetingServiceTest {

    @ArquillianResource
    private URL url;

    @Test
    public void should_get_greetings() {

        given()
            .get(url)
            .then()
            .assertThat()
            .body(CoreMatchers.is("Hello World"));

    }

}
