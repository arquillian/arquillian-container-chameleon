package org.arquillian.container.chameleon.runner.fixtures;

import org.arquillian.container.chameleon.api.ChameleonTarget;
import org.arquillian.container.chameleon.api.Property;

@ChameleonTarget(container = "tomcat", version = "7.0.0", customProperties = {
    @Property(name="a", value="b")
})
public class GenericTest {
}
