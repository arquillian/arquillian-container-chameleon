package org.arquillian.container.chameleon;

import org.arquillian.container.chameleon.api.ChameleonTarget;

import java.lang.annotation.*;

@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ChameleonTarget("tomee:7.0.5:managed")
public @interface Tomee {
}
