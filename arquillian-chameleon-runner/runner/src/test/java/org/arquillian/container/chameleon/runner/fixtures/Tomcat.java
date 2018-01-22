package org.arquillian.container.chameleon.runner.fixtures;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.arquillian.container.chameleon.api.ChameleonTarget;

@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ChameleonTarget("tomcat:7.0.0:managed")
public @interface Tomcat {
}
